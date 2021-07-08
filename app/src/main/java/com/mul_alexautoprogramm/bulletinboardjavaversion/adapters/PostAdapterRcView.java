package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.NewPost;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostAdapterRcView extends RecyclerView.Adapter<PostAdapterRcView.AdsViewHolder> {
    private List<NewPost> arrayListPost;
    private Context context;
    private onItemClickCustom onItemClickCustom;

    public PostAdapterRcView(List<NewPost> arrayListPost, Context context, onItemClickCustom onItemClickCustom) {
        this.arrayListPost = arrayListPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;
    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
        return new AdsViewHolder(view, onItemClickCustom);

    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {

        holder.setData(arrayListPost.get(position));

    }

    @Override
    public int getItemCount() {

        return arrayListPost.size();

    }

    public class AdsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvItemTitle,tvItemPrice,tvItemTelNumb,tvItemDescription;
        private ImageView imItemView;
        private onItemClickCustom onItemClickCustom;


        public AdsViewHolder(@NonNull View itemView, onItemClickCustom onItemClickCustom) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemTelNumb = itemView.findViewById(R.id.tvItemTelNumb);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            imItemView = itemView.findViewById(R.id.imAdsItem);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;

        }

        public void setData(NewPost newPost){

            Picasso.get().load(newPost.getImId()).into(imItemView);

            tvItemTitle.setText(newPost.getTitle());
            tvItemPrice.setText(newPost.getPrice());
            tvItemTelNumb.setText(newPost.getTel_numb());
            tvItemDescription.setText(newPost.getDesc());

        }



        @Override
        public void onClick(View v) {

            onItemClickCustom.onItemSelected(getAdapterPosition());

        }
    }

    public interface onItemClickCustom{

        void onItemSelected(int position);

    }

    public void updateAdapter(List<NewPost> listData){

        arrayListPost.clear();
        arrayListPost.addAll(listData);
        notifyDataSetChanged();

    }

}
