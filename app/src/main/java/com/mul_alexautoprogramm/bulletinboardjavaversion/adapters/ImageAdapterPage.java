package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.ImagesManager;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.OnBitmapLoaded;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAdapterPage extends PagerAdapter implements OnBitmapLoaded {
    private Activity context;
    private LayoutInflater layoutInflater;
    private List<Bitmap> bitmapList;
    private ImagesManager imagesManager;


    public ImageAdapterPage(Activity context) {
        this.context = context;
        imagesManager = new ImagesManager(context, this);
        layoutInflater = LayoutInflater.from(context);
        bitmapList = new ArrayList<>();

    }

    //inflate PagerItem
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.pager_view_item, container, false);
        ImageView imageViewItem = view.findViewById(R.id.imViewPager);

        imageViewItem.setImageBitmap(bitmapList.get(position));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {

        return bitmapList.size();

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void updateImages(List<String> images){

        imagesManager.resizeMultiLargeImages(images);

    }

    @Override
    public void onBitmapLoaded(final List<Bitmap> bitmap) {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                bitmapList.clear();
                bitmapList.addAll(bitmap);
                notifyDataSetChanged();

            }
        });

    }

}
