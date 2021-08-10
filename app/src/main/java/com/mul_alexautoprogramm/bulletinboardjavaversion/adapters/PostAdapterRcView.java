package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.DbManager;
import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.FavoritesPathItem;
import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.OnFavReceivedListener;
import com.mul_alexautoprogramm.bulletinboardjavaversion.EditActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.MainActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.NewPost;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.ShowLayoutActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostAdapterRcView extends RecyclerView.Adapter<PostAdapterRcView.AdsViewHolder> implements OnFavReceivedListener {
    public static final String TAG = "MyLog";
    private List<NewPost> mainPostList;
    private Context context;
    private onItemClickCustom onItemClickCustom;
    private DbManager dbManager;
    private List<FavoritesPathItem> favoritesPathItemList;



    public PostAdapterRcView(List<NewPost> arrayListPost, Context context, onItemClickCustom onItemClickCustom) {

        this.mainPostList = arrayListPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;
        favoritesPathItemList = new ArrayList<>();

    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
        return new AdsViewHolder(view, onItemClickCustom);

    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {

        holder.setData(mainPostList.get(position));
        setFavIfSelected(holder);

    }

    @Override
    public int getItemCount() {

        return mainPostList.size();

    }

    @Override
    public void onFavReceived(List<FavoritesPathItem> items) {

        //Log.d(TAG, "Items received: " + items.size());
        favoritesPathItemList.clear();
        favoritesPathItemList.addAll(items);
        notifyDataSetChanged();

    }
    private void deleteDialog(final NewPost newPost, int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_title);
        builder.setMessage(R.string.delete_message);

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dbManager.deleteItem(newPost);
                mainPostList.remove(position);
                notifyItemRemoved(position);

            }
        });



        builder.show();

    }


    public interface onItemClickCustom{

        void onItemSelected(int position);

    }

    public void updateAdapter(List<NewPost> listData){

        mainPostList.clear();
        mainPostList.addAll(listData);
        notifyDataSetChanged();

    }

    public void setDbManager(DbManager dbManager){

        this.dbManager =  dbManager;
        dbManager.setOnFavReceivedListener(this);


    }

    private void setFavIfSelected(AdsViewHolder holder){

        boolean isFav = false;
        for(FavoritesPathItem item: favoritesPathItemList){

            if(item.getFavoritesPath().equals(holder.favPath)){

                isFav = true;
                break;

            }

        }

        if(isFav){

            holder.imFavorites.setImageResource(R.drawable.ic_fav_selected);

        }else {

            holder.imFavorites.setImageResource(R.drawable.ic_fav_not_selected);

        }

    }

    //ViewHolderClass

    public class AdsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvItemTitle,tvItemPrice,tvItemTelNumb,tvItemDescription, tvTotal_views;
        private ImageView imItemView;
        private LinearLayout editLayout;
        private onItemClickCustom onItemClickCustom;
        private ImageButton imEditItem;
        private ImageButton imDeleteItem;
        private ImageButton imFavorites;
        public String favPath;


        public AdsViewHolder(@NonNull View itemView, onItemClickCustom onItemClickCustom) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemTelNumb = itemView.findViewById(R.id.tvItemTelNumb);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            imItemView = itemView.findViewById(R.id.imAdsItem);
            editLayout = itemView.findViewById(R.id.editLayout);

            imDeleteItem = itemView.findViewById(R.id.imDeleteItem);
            imEditItem = itemView.findViewById(R.id.imEditItem);
            imFavorites = itemView.findViewById(R.id.imFav);
            tvTotal_views = itemView.findViewById(R.id.tvTotalView);


            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;

        }

        public void setData(NewPost newPost){

            if(newPost.getUid().equals(MainActivity.MAUTH)){

                editLayout.setVisibility(View.VISIBLE);

            }else{

                editLayout.setVisibility(View.GONE);

            }
            Picasso.get().load(newPost.getImId()).into(imItemView);
            favPath = newPost.getCategory() + "/" + newPost.getKey() + "/" + newPost.getUid() + "/" + "Ads";
            tvItemTitle.setText(newPost.getTitle());
            tvItemPrice.setText(newPost.getPrice());
            tvItemTelNumb.setText(newPost.getTel_numb());
            String textDisc;
            if(newPost.getDesc().length() > 50){

                textDisc = newPost.getDesc().substring(0,50) + "...";

            }else{

                textDisc =  newPost.getDesc();

            }
            tvItemDescription.setText(textDisc);
            tvTotal_views.setText(newPost.getTotalViews());


            //DeleteButton/
            imDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteDialog(newPost, getAdapterPosition());

                }
            });

            //EditButton/
            imEditItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra(MyConstance.NEW_POST_INTENT, newPost);
                    i.putExtra(MyConstance.EDIT_STATE, true);
                    context.startActivity(i);

                }
            });

            //FavButton
            imFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbManager.updateFavorites(favPath);

                }
            });

        }



        @Override
        public void onClick(View v) {

            NewPost newPost = mainPostList.get(getAdapterPosition());

            dbManager.updateTotalViews(newPost);


            int total_views = Integer.parseInt(newPost.getTotalViews());
            total_views++;
            newPost.setTotalViews(String.valueOf(total_views));

            Intent i = new Intent(context, ShowLayoutActivity.class);
            i.putExtra(MyConstance.NEW_POST_INTENT, newPost);
            context.startActivity(i);
            onItemClickCustom.onItemSelected(getAdapterPosition());

        }
    }


    public List<FavoritesPathItem> getFavoritesPathItemList() {
        return favoritesPathItemList;
    }

    public void clearAdapter(){

        mainPostList.clear();
        notifyDataSetChanged();

    }

}
