package com.mul_alexautoprogramm.bulletinboardjavaversion;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.DataSender;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Context context;
    private Query mQuery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private  FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private int categoryAdsCounter = 0;
    private String[] myCategoryAds = {"Cars", "Personal computers", "Smartphone", "Appliances"};
    private int deleteImageCounter = 0;


    public void updateTotalViews(final NewPost newPost){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(newPost.getCategory());
        int total_views;
        try {

            total_views = Integer.parseInt(newPost.getTotalViews());



        }catch (NumberFormatException e ){
            total_views = 0;
        }
        total_views++;
        databaseReference.child(newPost.getKey()).child("Ads/totalViews").setValue(String.valueOf(total_views));
    }
    //?????
    public void deleteItem(final NewPost newPost){
        StorageReference storageReference = null;

        switch (deleteImageCounter){

            case 0:
                if(!newPost.getImId().equals("empty")) {
                    storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId());
                }else {
                    deleteImageCounter++;
                    deleteItem(newPost);
                }
                break;
            case 1:
                if(!newPost.getImId2().equals("empty")) {
                    storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId2());
                }else {
                    deleteImageCounter++;
                    deleteItem(newPost);
                }
                break;
            case 2:
                if(!newPost.getImId3().equals("empty")) {
                    storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId3());
                }else {
                    deleteDbItem(newPost);
                    storageReference = null;
                    deleteImageCounter = 0;
                }
                break;
        }
        if(storageReference == null) return;

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                deleteImageCounter++;
                if(deleteImageCounter < 3){

                    deleteItem(newPost);

                }else{

                    deleteImageCounter = 0;
                    deleteDbItem(newPost);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.error_item_delete, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteDbItem(NewPost newPost){

        DatabaseReference databaseReference = firebaseDatabase.getReference(newPost.getCategory());
        databaseReference.child(newPost.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(context, R.string.item_done_delete, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, R.string.error_item_delete, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public DbManager(DataSender dataSender, Context context) {
        this.dataSender = dataSender;
        this.context = context;
        newPostList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    public void getDataFromDb(String path) {

        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        //Query orders our announcements by time
        mQuery = databaseReference.orderByChild("Ads/time");
        readDataUpdate();

    }

    //getMyAds
    public void getMyAdsDataFromDb(String uid) {
        if(newPostList.size() > 0) newPostList.clear();
        DatabaseReference databaseReference = firebaseDatabase.getReference(myCategoryAds[0]);
        //Query orders our announcements by uid
        mQuery = databaseReference.orderByChild("Ads/uid").equalTo(uid);
        readMyAdsDataUpdate(uid);
        categoryAdsCounter++;

    }

    public void readDataUpdate(){

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(newPostList.size() > 0) newPostList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    NewPost newPost = dataSnapshot.child("Ads").getValue(NewPost.class);
                    newPostList.add(newPost);

                }
                dataSender.onDataRecived(newPostList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void readMyAdsDataUpdate(final String uid){

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    NewPost newPost = dataSnapshot.child("Ads").getValue(NewPost.class);
                    newPostList.add(newPost);

                }
                if(categoryAdsCounter > 3){

                    dataSender.onDataRecived(newPostList);
                    newPostList.clear();
                    categoryAdsCounter = 0;

                }else{

                    DatabaseReference databaseReference = firebaseDatabase.getReference(myCategoryAds[categoryAdsCounter]);
                    //Query orders our announcements by uid
                    mQuery = databaseReference.orderByChild("Ads/uid").equalTo(uid);
                    readMyAdsDataUpdate(uid);
                    categoryAdsCounter++;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
