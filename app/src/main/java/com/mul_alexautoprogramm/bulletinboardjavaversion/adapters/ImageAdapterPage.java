package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mul_alexautoprogramm.bulletinboardjavaversion.R;

import java.util.Collections;

public class ImageAdapterPage extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    int[] imageArray = {R.drawable.imge_test_1, R.drawable.image_test_2, R.drawable.image_test_3};


    public ImageAdapterPage(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    //inflate PagerItem
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.pager_view_item, container, false);
        ImageView imageViewItem = view.findViewById(R.id.imViewPager);
        imageViewItem.setImageResource(imageArray[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

}
