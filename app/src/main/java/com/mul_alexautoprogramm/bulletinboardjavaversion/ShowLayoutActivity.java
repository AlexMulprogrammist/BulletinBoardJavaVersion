package com.mul_alexautoprogramm.bulletinboardjavaversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.adapters.ImageAdapterPage;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowLayoutActivity extends AppCompatActivity {
    private TextView tvTitle, tvPrice, tvDisc, tvTelNumb;
    private ImageView imMainShow;
    private List<String> imagesUris;
    private ImageAdapterPage imageAdapterPage;
    private TextView tvImagesCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout_activity);
        init();
    }

    private void init(){
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

        if(getIntent() != null){

            Intent i = getIntent();
            tvTitle.setText(i.getStringExtra(MyConstance.TITLE));
            tvPrice.setText(i.getStringExtra(MyConstance.PRICE));
            tvTelNumb.setText(i.getStringExtra(MyConstance.TEL_NUMB));
            tvDisc.setText(i.getStringExtra(MyConstance.DESC));

            String[] images = new String[3];
            images[0] = i.getStringExtra(MyConstance.IMAGE_ID);
            images[1] = i.getStringExtra(MyConstance.IMAGE_ID_2);
            images[2] = i.getStringExtra(MyConstance.IMAGE_ID_3);

            for(String s : images){

                if(!s.equals("empty")) imagesUris.add(s);

            }

            imageAdapterPage.setFirebaseUri(true);
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

}