package com.mul_alexautoprogramm.bulletinboardjavaversion;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.DataSender;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Query mQuery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private  FirebaseDatabase firebaseDatabase;
    private int categoryAdsCounter = 0;
    private String[] myCategoryAds = {"Cars", "Personal computers", "Smartphone", "Appliances"};

    public DbManager(DataSender dataSender) {
        this.dataSender = dataSender;
        newPostList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
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
