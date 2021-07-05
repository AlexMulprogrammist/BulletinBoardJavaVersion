package com.mul_alexautoprogramm.bulletinboardjavaversion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditActivity extends AppCompatActivity {
    private ImageView imItem;
    private StorageReference myStorageRef;
    private Uri uploadUri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity_layout);
        init();

    }

    private void init(){

        imItem = findViewById(R.id.imItem);
        myStorageRef = FirebaseStorage.getInstance().getReference("Images");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && data != null && data.getData() != null){

            if(resultCode == RESULT_OK){
                //We got a picture and show it in our ImageView
                imItem.setImageURI(data.getData());
                upLoadImage();
            }

        }

    }

    private void upLoadImage(){

        Bitmap bitmap = ((BitmapDrawable)imItem.getDrawable()).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] byteArray = out.toByteArray();
        final StorageReference myReference = myStorageRef.child(System.currentTimeMillis() + "_image");
        UploadTask uploadTask = myReference.putBytes(byteArray);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return myReference.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadUri = task.getResult();
                assert uploadUri != null;
                Toast.makeText(EditActivity.this, "Upload Done : " + uploadUri.toString(), Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

            }

        });

    }

    //onClickImage in EditAct
    public void onClickImageViewEditAct(View view){

        getImage();

    }

    //Request to get a picture
    private void getImage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 10);

    }


}
