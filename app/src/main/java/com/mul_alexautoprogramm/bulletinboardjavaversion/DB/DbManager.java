package com.mul_alexautoprogramm.bulletinboardjavaversion.DB;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private String[] myCategoryAds = {"Cars", "Personal computers", "Smartphone", "Appliances"};
    private int deleteImageCounter = 0;
    private FirebaseAuth myAuth;
    public static final String MY_ACCOUNT_PATH = "accounts";
    public static final String MY_FAVORITES_PATH = "my_favorites";
    public static final String MY_ADS_FAVORITES_PATH = "my_ads_favorites_path";
    private List<FavoritesPathItem> favoritesPathItemList;
    private OnFavReceivedListener onFavReceivedListener;


    public void updateTotalViews(final NewPost newPost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(newPost.getCategory());
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

        databaseReference.child(newPost.getKey()).child("status").setValue(statusItem);
    }

    public void updateTotalEmails(final NewPost newPost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(newPost.getCategory());
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

        databaseReference.child(newPost.getKey()).child("status").setValue(statusItem);
    }

    public void updateTotalCalls(final NewPost newPost) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(newPost.getCategory());
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

        databaseReference.child(newPost.getKey()).child("status").setValue(statusItem);
    }

    public void updateFavorites(final String favorite_path) {
        if (myAuth.getUid() == null) return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MY_ACCOUNT_PATH);
        String key = databaseReference.push().getKey();


        if (key == null) return;
        boolean isFavorites = false;
        String keyToDelete = "";
        for(FavoritesPathItem favoritesPathItem : favoritesPathItemList){

            if(favoritesPathItem.getFavoritesPath().equals(favorite_path)){
                isFavorites = true;
                keyToDelete = favoritesPathItem.getKey();
            }

        }


        if(isFavorites){

            deleteFavorites(keyToDelete);

        }else {

            FavoritesPathItem favoritesPathItem = new FavoritesPathItem();
            favoritesPathItem.setKey(key);
            favoritesPathItem.setFavoritesPath(favorite_path);
            databaseReference.child(myAuth.getUid()).child(MY_FAVORITES_PATH).child(key).child(MY_ADS_FAVORITES_PATH).setValue(favoritesPathItem);

        }

    }

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

        DatabaseReference databaseReference = firebaseDatabase.getReference(newPost.getCategory());
        databaseReference.child(newPost.getKey()).child(newPost.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

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
        favoritesPathItemList = new ArrayList<>();

    }

    public void getDataFromDb(String path) {
        if (myAuth.getUid() != null) {

            Log.d("MyLog", "getDataFromDb myAuth.getUid() != null" + myAuth.getUid());

            DatabaseReference databaseReference = firebaseDatabase.getReference(path);
            //Query orders our announcements by time
            mQuery = databaseReference.orderByChild("Ads/time");
            readDataUpdate();

        }

    }

    public void getMyAdsDataFromDb(String uid) {

        if(myAuth.getUid() == null) return;
        if (newPostList.size() > 0) newPostList.clear();
        DatabaseReference databaseReference = firebaseDatabase.getReference(myCategoryAds[0]);
        //Query orders our announcements by uid
        mQuery = databaseReference.orderByChild(myAuth.getUid() + "/Ads/uid").equalTo(uid);
        readMyAdsDataUpdate(uid);
        categoryAdsCounter++;

    }

    public void readDataUpdate() {
        if (myAuth.getUid() != null) {
            mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (newPostList.size() > 0) newPostList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        //NewPost newPost = dataSnapshot.child(myAuth.getUid() + "/Ads").getValue(NewPost.class);
                        NewPost newPost = dataSnapshot.getChildren().iterator().next().child("Ads").getValue(NewPost.class);
                        StatusItem statusItem = dataSnapshot.child("status").getValue(StatusItem.class);
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

    public void readMyFavoritesDataUpdate(List<FavoritesPathItem> favList) {

        if (myAuth.getUid() != null) {

            DatabaseReference databaseReference = firebaseDatabase.getReference();
            List<NewPost> tempFavList = new ArrayList<>();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    tempFavList.clear();
                    for (FavoritesPathItem item : favList) {

                        NewPost newPost = snapshot.child(item.getFavoritesPath()).getValue(NewPost.class);

                        String[] statusArrayPath = item.getFavoritesPath().split("/");
                        StatusItem statusItem = snapshot.child(statusArrayPath[0])
                                .child(statusArrayPath[1])
                                .child("status").getValue(StatusItem.class);

                      if (newPost != null && statusItem != null) {

                            newPost.setTotalViews(statusItem.totalViews);
                            newPost.setTotalEmails(statusItem.totalEmails);
                            newPost.setTotalCalls(statusItem.totalCalls);

                        }

                        tempFavList.add(newPost);

                    }
                    dataSender.onDataRecived(tempFavList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


    public void readMyAdsDataUpdate(final String uid) {


        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    NewPost newPost = dataSnapshot.child(myAuth.getUid() + "/Ads").getValue(NewPost.class);

                    StatusItem statusItem = dataSnapshot.child("status").getValue(StatusItem.class);
                    if (newPost != null && statusItem != null) {

                        newPost.setTotalViews(statusItem.totalViews);
                        newPost.setTotalEmails(statusItem.totalEmails);
                        newPost.setTotalCalls(statusItem.totalCalls);

                    }

                    newPostList.add(newPost);

                }
                if (categoryAdsCounter > 3) {

                    dataSender.onDataRecived(newPostList);
                    newPostList.clear();
                    categoryAdsCounter = 0;

                } else {

                    DatabaseReference databaseReference = firebaseDatabase.getReference(myCategoryAds[categoryAdsCounter]);
                    //Query orders our announcements by uid
                    mQuery = databaseReference.orderByChild(myAuth.getUid() + "/Ads/uid").equalTo(uid);
                    readMyAdsDataUpdate(uid);
                    categoryAdsCounter++;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void readFavorites() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MY_ACCOUNT_PATH);
        if (myAuth.getUid() == null) return;
        databaseReference.child(myAuth.getUid()).child(MY_FAVORITES_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritesPathItemList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    favoritesPathItemList.add(ds.child(MY_ADS_FAVORITES_PATH).getValue(FavoritesPathItem.class));

                }
                onFavReceivedListener.onFavReceived(favoritesPathItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteFavorites(String key){
        if(myAuth.getUid() == null) return;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MY_ACCOUNT_PATH);
        databaseReference.child(myAuth.getUid()).child(MY_FAVORITES_PATH).child(key).removeValue();

    }

    public void setOnFavReceivedListener(OnFavReceivedListener onFavReceivedListener){

        this.onFavReceivedListener = onFavReceivedListener;

    }

}

