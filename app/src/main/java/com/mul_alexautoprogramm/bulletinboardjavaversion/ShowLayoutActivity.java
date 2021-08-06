package com.mul_alexautoprogramm.bulletinboardjavaversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.DbManager;
import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.NewPost;
import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.ImageAdapterPage;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;

import java.util.ArrayList;
import java.util.List;

public class ShowLayoutActivity extends AppCompatActivity {
    private TextView tvTitle, tvPrice, tvDisc, tvTelNumb, tvEmail;
    private ImageView imMainShow;
    private List<String> imagesUris;
    private ImageAdapterPage imageAdapterPage;
    private TextView tvImagesCounter;
    private String tell;
    private NewPost newPost;
    private DbManager dbManager;
    private boolean isTotalEmailsAdded = false;
    private boolean isTotalCallsAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout_activity);
        init();
    }

    private void init(){

        dbManager = new DbManager(null, this);
        tvImagesCounter = findViewById(R.id.tvImagesCounter);
        imagesUris = new ArrayList<>();
        ViewPager viewPager = findViewById(R.id.view_pager);
        imageAdapterPage = new ImageAdapterPage(this);
        viewPager.setAdapter(imageAdapterPage);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvTitle = findViewById(R.id.tvTitleShow);
        tvPrice = findViewById(R.id.tvPriceShowDescript);
        tvDisc = findViewById(R.id.tvDescShow);
        tvTelNumb = findViewById(R.id.tvTelNumbDescShow);
        tvEmail = findViewById(R.id.tvEmail);

        if(getIntent() != null){

            Intent i = getIntent();
            newPost = (NewPost) i.getSerializableExtra(MyConstance.NEW_POST_INTENT);
            if(newPost == null) return;
            tvTitle.setText(newPost.getTitle());
            tvPrice.setText(newPost.getPrice());
            tvTelNumb.setText(newPost.getTel_numb());
            tvEmail.setText(newPost.getEmail());
            tvDisc.setText(newPost.getDesc());


            tell = newPost.getTel_numb();

            String[] images = new String[3];
            images[0] = newPost.getImId();
            images[1] = newPost.getImId2();
            images[2] = newPost.getImId3();

            for(String s : images){

                if(!s.equals("empty")) imagesUris.add(s);

            }
            imageAdapterPage.updateImages(imagesUris);

            if(imagesUris.size() > 0) {
                String dataText = 1 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);
            }else {
                imagesUris.size();
                String dataText = 0 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);
            }

            //Picasso.get().load(i.getStringExtra(MyConstance.IMAGE_ID)).into(imMainShow);

        }

    }

    public void onClickCall(View view){

        if(!isTotalCallsAdded){

            dbManager.updateTotalCalls(newPost);
            int total_Calls = Integer.parseInt(newPost.getTotalCalls());
            total_Calls++;
            newPost.setTotalCalls(String.valueOf(total_Calls));

            isTotalCallsAdded = true;
        }



        String telTemp = "tel:" + tell;
        Intent iCall = new Intent(Intent.ACTION_DIAL);
        iCall.setData(Uri.parse(telTemp));
        startActivity(iCall);


    }

    public void onClickEmailMessage(View view){
        if(!isTotalEmailsAdded){

            dbManager.updateTotalEmails(newPost);
            int total_Emails = Integer.parseInt(newPost.getTotalEmails());
            total_Emails++;
            newPost.setTotalEmails(String.valueOf(total_Emails));
            isTotalEmailsAdded = true;

        }


        Intent iEmailMessage = new Intent(Intent.ACTION_SEND);
        iEmailMessage.setType("message/rfc822");
        iEmailMessage.putExtra(Intent.EXTRA_EMAIL, new String[]{newPost.getEmail()});
        iEmailMessage.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.extra_subject));
        iEmailMessage.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.extra_text));


        try {

            startActivity(Intent.createChooser(iEmailMessage, getResources().getString(R.string.to_open_with)));

        }catch (ActivityNotFoundException e ){
            Toast.makeText(this, getResources().getString(R.string.dont_have_app_message), Toast.LENGTH_SHORT).show();
        }


    }

}