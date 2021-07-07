package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.NewPost;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;

import java.util.ArrayList;
import java.util.List;

public class PostAdapterRcView extends RecyclerView.Adapter<PostAdapterRcView.AdsViewHolder> {
    private List<NewPost> arrayListPost;
    private Context context;

    public PostAdapterRcView(List<NewPost> arrayListPost, Context context) {
        this.arrayListPost = arrayListPost;
        this.context = context;
    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {

        return arrayListPost.size();

    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle,tvItemPrice,tvItemTelNumb,tvItemDescription;
        private ImageView imItemView;


        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemTelNumb = itemView.findViewById(R.id.tvItemTelNumb);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            imItemView = itemView.findViewById(R.id.imAdsItem);

        }

        public void setData(NewPost newPost){

            tvItemTitle.setText(newPost.getTitle());
            tvItemPrice.setText(newPost.getPrice());
            tvItemTelNumb.setText(newPost.getTel_numb());
            tvItemDescription.setText(newPost.getDesc());

        }

    }
}
