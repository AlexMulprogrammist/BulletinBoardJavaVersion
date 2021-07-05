package com.mul_alexautoprogramm.bulletinboardjavaversion;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {
    private ImageView imItem;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity_layout);
        init();

    }

    private void init(){

        imItem = findViewById(R.id.imItem);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && data != null && data.getData() != null){

            if(resultCode == RESULT_OK){

                imItem.setImageURI(data.getData());

            }

        }

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
