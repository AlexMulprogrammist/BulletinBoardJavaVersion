package com.mul_alexautoprogramm.bulletinboardjavaversion.screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.squareup.picasso.Picasso;

public class ChooseImagesActivity extends AppCompatActivity {

    private ImageView imMain,im_2,im_3;
    private ImageView[] imagesViews = new ImageView[3];
    private String[] uris = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_images);
        init();
        getMyIntent();

    }

    private void init(){


        imMain = findViewById(R.id.imMainChosse);
        im_2 = findViewById(R.id.image2Choose);
        im_3 = findViewById(R.id.image3Choose);
        /*uris[0] = "empty";
        uris[1] = "empty";
        uris[2] = "empty";*/
        imagesViews[0] = imMain;
        imagesViews[1] = im_2;
        imagesViews[2] = im_3;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null && data.getData() != null){

            switch (requestCode){

                case 1:
                     uris[0] = data.getData().toString();
                    imMain.setImageURI(data.getData());
                    break;
                case 2:
                    uris[1] = data.getData().toString();
                    im_2.setImageURI(data.getData());
                    break;
                case 3:
                    uris[2] = data.getData().toString();
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

    private void getMyIntent(){
        Intent i = getIntent();
        if(i != null){

            uris[0] = i.getStringExtra(MyConstance.IMAGE_ID);
            uris[1] = i.getStringExtra(MyConstance.IMAGE_ID_2);
            uris[2] = i.getStringExtra(MyConstance.IMAGE_ID_3);

            setImages(uris);

        }

    }

    private void setImages(String[] uris){

        for(int i = 0; i < uris.length; i++){

            if(!uris[i].equals("empty")){

                showImages(uris[i], i);

            }

        }


    }

    private void showImages(String uri, int position){

        if(uri.substring(0,4).equals("http")){

            Picasso.get().load(uri).into(imagesViews[position]);

        }else {

            imagesViews[position].setImageURI(Uri.parse(uri));

        }

    }


    public void onClickDone(View view) {
        Intent i = new Intent();
        i.putExtra("uri_main", uris[0]);
        i.putExtra("uri_2", uris[1]);
        i.putExtra("uri_3", uris[2]);
        setResult(RESULT_OK, i);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    public void onClickDeleteMainImage(View view) {

        imMain.setImageResource(R.drawable.ic_add_image);
        uris[0] = "empty";


    }

    public void onClickDeleteImage2(View view) {

        im_2.setImageResource(R.drawable.ic_add_image);
        uris[1] = "empty";

    }

    public void onClickDeleteImage3(View view) {

        im_3.setImageResource(R.drawable.ic_add_image);
        uris[2] = "empty";

    }
}