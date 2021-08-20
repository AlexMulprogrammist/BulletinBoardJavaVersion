package com.mul_alexautoprogramm.bulletinboardjavaversion.DB;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.DataSender;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.PostAdapterRcView;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Context context;
    private Query mQuery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private int categoryAdsCounter = 0;
    private int deleteImageCounter = 0;
    private FirebaseAuth myAuth;
    public static final String MY_ACCOUNT_PATH = "accounts";
    public static final String MY_FAVORITES_PATH = "my_favorites";
    public static final String FAVORITES_ADS_PATH = "favorites_ads_path";
    public static final String USER_FAVORITES_ID = "user_favorites_id";
    public static final String MAIN_ADS_PATH = "main_ads_path";
    public static final String ORDER_BY_CAT_TIME = "/status/cat_time";
    public static final String ORDER_BY_TIME = "/status/filter_by_time";




    public void deleteItem(final NewPost newPost) {
        StorageReference storageReference = null;

        switch (deleteImageCounter) {

            case 0:
                if (!newPost.getImId().equals("empty")) {
                    storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId());
                } else {
                    deleteImageCounter++;
                    deleteItem(newPost);
                }
                break;
            case 1:
                if (!newPost.getImId2().equals("empty")) {
                    storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId2());
                } else {
                    deleteImageCounter++;
                    deleteItem(newPost);
                }
                break;
            case 2:
                if (!newPost.getImId3().equals("empty")) {
                    storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId3());
                } else {
                    deleteDbItem(newPost);
                    storageReference = null;
                    deleteImageCounter = 0;
                }
                break;
        }
        if (storageReference == null) return;

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("MyLog", "Log1");
                deleteImageCounter++;
                if (deleteImageCounter < 3) {
                    Log.d("MyLog", "Log2 image counter=" + deleteImageCounter);
                    deleteItem(newPost);

                } else {
                    Log.d("MyLog", "Log3 image counter=" + deleteImageCounter);
                    deleteImageCounter = 0;
                    deleteDbItem(newPost);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MyLog", "Log4 image counter=" + deleteImageCounter);
                Toast.makeText(context, R.string.error_item_delete, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteDbItem(NewPost newPost) {

        DatabaseReference databaseReference = firebaseDatabase.getReference(DbManager.MAIN_ADS_PATH);
        databaseReference.child(newPost.getKey()).child("status").removeValue();
        databaseReference.child(newPost.getKey()).child(myAuth.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context, R.string.item_done_delete, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, "Delete item from DB Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public DbManager(DataSender dataSender, Context context) {

        this.dataSender = dataSender;
        this.context = context;
        newPostList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        myAuth = FirebaseAuth.getInstance();


    }

    public void getDataFromDb(String category, String lastTime) {

        if (myAuth.getUid() == null)  return;
        DatabaseReference databaseReference = firebaseDatabase.getReference(MAIN_ADS_PATH);

        if(category.equals(MyConstance.ALL_CAT)){

            mQuery = databaseReference.orderByChild(ORDER_BY_TIME);

        }else if(category.equals(MyConstance.MY_ADS)){

            mQuery = databaseReference.orderByChild(myAuth.getUid() + "/Ads/uid").equalTo(myAuth.getUid());

        }else if(category.equals(MyConstance.MY_FAV)){

            mQuery = databaseReference.orderByChild(FAVORITES_ADS_PATH + "/" + myAuth.getUid() + "/" + USER_FAVORITES_ID).equalTo(myAuth.getUid());

        }else {

            if(lastTime.equals("0")){
                mQuery = databaseReference.orderByChild(ORDER_BY_CAT_TIME).startAt(category).endAt(category + "\uf8ff");
            }else {
                mQuery = databaseReference.orderByChild(ORDER_BY_CAT_TIME).startAt(category).endAt(category + "\uf8ff");
            }

        }

        readDataUpdate();

    }

    public void readDataUpdate() {

        if (myAuth.getUid() != null) {

            mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (newPostList.size() > 0) newPostList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        NewPost newPost = null;
                        //NewPost newPost = dataSnapshot.child(myAuth.getUid() + "/Ads").getValue(NewPost.class);
                        for(DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()){

                            if(newPost == null) newPost = dataSnapshot2.child("Ads").getValue(NewPost.class);

                        }

                        StatusItem statusItem = dataSnapshot.child("status").getValue(StatusItem.class);

                        String favUID = (String) dataSnapshot.child(FAVORITES_ADS_PATH).child(myAuth.getUid()).child(USER_FAVORITES_ID).getValue();
                        if(newPost != null) newPost.setFavCounter(dataSnapshot.child(FAVORITES_ADS_PATH).getChildrenCount());
                        if(favUID != null && newPost != null){
                            newPost.setFav(true);
                        }


                        if (newPost != null && statusItem != null) {

                            newPost.setTotalViews(statusItem.totalViews);
                            newPost.setTotalEmails(statusItem.totalEmails);
                            newPost.setTotalCalls(statusItem.totalCalls);

                        }

                        newPostList.add(newPost);

                    }
                    dataSender.onDataRecived(newPostList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void updateTotalViews(final NewPost newPost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        int total_views;
        try {
            total_views = Integer.parseInt(newPost.getTotalViews());
        } catch (NumberFormatException e) {
            total_views = 0;
        }
        total_views++;
        StatusItem statusItem = new StatusItem();
        statusItem.totalViews = String.valueOf(total_views);
        statusItem.totalCalls = newPost.getTotalCalls();
        statusItem.totalEmails = newPost.getTotalEmails();
        statusItem.cat_time = newPost.getCategory() + "_" + newPost.getTime();

        databaseReference.child(newPost.getKey()).child("status").setValue(statusItem);
    }

    public void updateTotalEmails(final NewPost newPost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        int total_emails;
        try {

            total_emails = Integer.parseInt(newPost.getTotalEmails());


        } catch (NumberFormatException e) {
            total_emails = 0;
        }
        total_emails++;
        StatusItem statusItem = new StatusItem();
        statusItem.totalEmails = String.valueOf(total_emails);
        statusItem.totalCalls = newPost.getTotalCalls();
        statusItem.totalViews = newPost.getTotalViews();
        statusItem.cat_time = newPost.getCategory() + "_" + newPost.getTime();

        databaseReference.child(newPost.getKey()).child("status").setValue(statusItem);
    }

    public void updateTotalCalls(final NewPost newPost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        int total_calls;
        try {

            total_calls = Integer.parseInt(newPost.getTotalCalls());


        } catch (NumberFormatException e) {
            total_calls = 0;
        }
        total_calls++;
        StatusItem statusItem = new StatusItem();
        statusItem.totalCalls = String.valueOf(total_calls);
        statusItem.totalEmails = newPost.getTotalEmails();
        statusItem.totalViews = newPost.getTotalViews();
        statusItem.cat_time = newPost.getCategory() + "_" + newPost.getTime();

        databaseReference.child(newPost.getKey()).child("status").setValue(statusItem);
    }

    public void updateFavorites(final NewPost newPost, PostAdapterRcView.AdsViewHolder holder) {

        if(newPost.isFav()){

            deleteFavorites(newPost, holder);

        }else {

            addFavorites(newPost, holder);

        }

    }
    
    private void addFavorites(final NewPost newPost, final PostAdapterRcView.AdsViewHolder holder){

        if (myAuth.getUid() == null) return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        databaseReference
                .child(newPost.getKey())
                .child(FAVORITES_ADS_PATH)
                .child(myAuth.getUid())
                .child(USER_FAVORITES_ID)
                .setValue(myAuth.getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            holder.imFavorites.setImageResource(R.drawable.ic_fav_selected);
                            newPost.setFav(true);
                        }
                    }
                });

    }

    private void deleteFavorites(final NewPost newPost, final PostAdapterRcView.AdsViewHolder holder){

        if(myAuth.getUid() == null) return;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MAIN_ADS_PATH);
        databaseReference.child(newPost.getKey()).child(FAVORITES_ADS_PATH).child(myAuth.getUid()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            holder.imFavorites.setImageResource(R.drawable.ic_fav_not_selected);
                            newPost.setFav(false);
                        }
                    }
                });


    }

}

