package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mul_alexautoprogramm.bulletinboardjavaversion.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAdapterPage extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> imagesUris;


    public ImageAdapterPage(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        imagesUris = new ArrayList<>();

    }

    //inflate PagerItem
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.pager_view_item, container, false);
        ImageView imageViewItem = view.findViewById(R.id.imViewPager);
        imageViewItem.setImageURI(Uri.parse(imagesUris.get(position)));
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return imagesUris.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void updateImages(List<String> images){

        imagesUris.clear();
        imagesUris.addAll(images);
        notifyDataSetChanged();

    }

}
