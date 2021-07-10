package com.mul_alexautoprogramm.bulletinboardjavaversion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.squareup.picasso.Picasso;

public class ShowLayoutActivity extends AppCompatActivity {
    private TextView tvTitle, tvPrice, tvDisc, tvTelNumb;
    private ImageView imMainShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout_activity);
        init();
    }

    private void init(){

        tvTitle = findViewById(R.id.tvTitleShow);
        tvPrice = findViewById(R.id.tvPriceShowDescript);
        tvDisc = findViewById(R.id.tvDescShow);
        tvTelNumb = findViewById(R.id.tvTelNumbDescShow);
        imMainShow = findViewById(R.id.imMainShow);

        if(getIntent() != null){

            Intent i = getIntent();
            tvTitle.setText(i.getStringExtra(MyConstance.TITLE));
            tvPrice.setText(i.getStringExtra(MyConstance.PRICE));
            tvTelNumb.setText(i.getStringExtra(MyConstance.TEL_NUMB));
            tvDisc.setText(i.getStringExtra(MyConstance.DESC));
            Picasso.get().load(i.getStringExtra(MyConstance.IMAGE_ID)).into(imMainShow);


        }

    }

}