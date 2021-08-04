package com.mul_alexautoprogramm.bulletinboardjavaversion.screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.ImagesManager;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.OnBitmapLoaded;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseImagesActivity extends AppCompatActivity {

    private ImageView imMain,im_2,im_3;
    private ImageView[] imagesViews = new ImageView[3];
    private String[] uris = new String[3];
    private ImagesManager imagesManager;
    private final int MAX_IMAGE_SIZE = 5000;
    private OnBitmapLoaded onBitmapLoaded;
    private boolean isImagesLoaded = true;

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
        uris[0] = "empty";
        uris[1] = "empty";
        uris[2] = "empty";
        imagesViews[0] = imMain;
        imagesViews[1] = im_2;
        imagesViews[2] = im_3;

        OnBitmapLoaded();
        imagesManager = new ImagesManager(this, onBitmapLoaded);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){

            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if(returnValue == null) return;

            switch (requestCode){

                case 1:

                     uris[0] = returnValue.get(0);
                     isImagesLoaded = false;
                     imagesManager.resizeMultiLargeImages(Arrays.asList(uris));

                    break;
                case 2:

                    uris[1] = returnValue.get(0);
                    isImagesLoaded = false;
                    imagesManager.resizeMultiLargeImages(Arrays.asList(uris));

                    break;
                case 3:

                    uris[2] = returnValue.get(0);
                    isImagesLoaded = false;
                    imagesManager.resizeMultiLargeImages(Arrays.asList(uris));

                    break;
            }

        }

    }

    private void OnBitmapLoaded(){
        onBitmapLoaded = new OnBitmapLoaded() {
            @Override
            public void onBitmapLoaded(List<Bitmap> bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < bitmap.size(); i++) {
                            if(bitmap.get(i) != null){

                                imagesViews[i].setImageBitmap(bitmap.get(i));

                            }
                            isImagesLoaded = true;
                        }

                    }
                });
            }
        };
    }

    public void onClickMainImage(View view) {
        if(!isImagesLoaded) {
            Toast.makeText(this, "Loading images..Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        getImage(1);

    }

    public void onClickImage2Choose(View view) {
        if(!isImagesLoaded) {
            Toast.makeText(this, "Loading images..Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        getImage(2);

    }

    public void onClickImage3Choose(View view) {
        if(!isImagesLoaded) {
            Toast.makeText(this, "Loading images..Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        getImage(3);

    }

    //Request to get a picture
    private void getImage(int index){

       /* Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, index);*/

        Options options = Options.init()
                .setRequestCode(index)
                .setCount(1)
                .setFrontfacing(false)
                .setExcludeVideos(true)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);

        Pix.start(ChooseImagesActivity.this, options);


    }

    private void getMyIntent(){
        Intent i = getIntent();
        if(i != null){

            uris[0] = i.getStringExtra(MyConstance.NEW_POST_INTENT);
            uris[1] = i.getStringExtra(MyConstance.IMAGE_ID_2);
            uris[2] = i.getStringExtra(MyConstance.IMAGE_ID_3);
            isImagesLoaded = false;
            imagesManager.resizeMultiLargeImages(sortImages(uris));

        }

    }

    private List<String> sortImages(String[] uris){

        List<String> tempList = new ArrayList<>();
        for(int i = 0; i < uris.length; i++){

            if(uris[i].startsWith("http")){

                showHTTPImages(uris[i], i);
                tempList.add("empty");

            }else {

                tempList.add(uris[i]);

            }

        }

        return tempList;

    }

    private void showHTTPImages(String uri, int position){

            Picasso.get().load(uri).into(imagesViews[position]);

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