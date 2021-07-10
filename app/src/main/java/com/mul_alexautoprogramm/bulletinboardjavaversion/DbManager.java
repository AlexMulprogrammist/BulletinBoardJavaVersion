package com.mul_alexautoprogramm.bulletinboardjavaversion;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public void deleteItem(final NewPost newPost){

        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(newPost.getImId());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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
