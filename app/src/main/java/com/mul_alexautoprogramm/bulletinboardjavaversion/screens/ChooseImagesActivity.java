package com.mul_alexautoprogramm.bulletinboardjavaversion.screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.R;

public class ChooseImagesActivity extends AppCompatActivity {
    private String uri_main = "empty", uri_2 = "empty", uri_3 = "empty";
    private ImageView imMain, im_2, im_3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_images);
        init();

    }

    private void init(){

        imMain = findViewById(R.id.imMainChosse);
        im_2 = findViewById(R.id.image2Choose);
        im_3 = findViewById(R.id.image3Choose);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null && data.getData() != null){

            switch (requestCode){

                case 1:
                    uri_main = data.getData().toString();
                    imMain.setImageURI(data.getData());
                    break;
                case 2:
                    uri_2 = data.getData().toString();
                    im_2.setImageURI(data.getData());
                    break;
                case 3:
                    uri_3 = data.getData().toString();
                    im_3.setImageURI(data.getData());
                    break;
            }

        }

    }

    public void onClickMainImage(View view) {

        getImage(1);

    }

    public void onClickImage2Choose(View view) {

        getImage(2);

    }

    public void onClickImage3Choose(View view) {

        getImage(3);

    }

    //Request to get a picture
    private void getImage(int index){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, index);

    }

    public void onClickDone(View view) {
        Intent i = new Intent();
        i.putExtra("uri_main", uri_main);
        i.putExtra("uri_2", uri_2);
        i.putExtra("uri_3", uri_3);
        setResult(RESULT_OK, i);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    public void onClickDeleteMainImage(View view) {

        imMain.setImageResource(R.drawable.ic_add_image);
        uri_main = "empty";


    }

    public void onClickDeleteImage2(View view) {

        im_2.setImageResource(R.drawable.ic_add_image);
        uri_2 = "empty";

    }

    public void onClickDeleteImage3(View view) {

        im_3.setImageResource(R.drawable.ic_add_image);
        uri_3 = "empty";

    }
}