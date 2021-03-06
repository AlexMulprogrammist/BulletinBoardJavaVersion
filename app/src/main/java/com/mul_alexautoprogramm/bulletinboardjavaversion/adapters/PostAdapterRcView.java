package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.mul_alexautoprogramm.bulletinboardjavaversion.EditActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.MainActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.DB.NewPost;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.ShowLayoutActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapterRcView extends RecyclerView.Adapter<PostAdapterRcView.AdsViewHolder>{
    public static final String TAG = "MyLog";
    private List<NewPost> mainPostList;
    private Context context;
    private onItemClickCustom onItemClickCustom;
    private DbManager dbManager;
    private int myViewType = 0;
    private int VIEW_TYPE_ADS = 0;
    private int VIEW_TYPE_END_BUTTON = 1;
    private boolean isStartPage = true;
    private final int NEXT_ADS_BUTTON = 1;
    private final int BACK_ADS_BUTTON = 2;
    private int adsButtonState = 0;




    public PostAdapterRcView(List<NewPost> arrayListPost, Context context, onItemClickCustom onItemClickCustom) {

        this.mainPostList = arrayListPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;


    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_END_BUTTON) {

            view = LayoutInflater.from(context).inflate(R.layout.end_ads_item, parent, false);

        }else {

            view = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);

        }
        Log.d("MyLog","Item type: " + viewType);
        return new AdsViewHolder(view, onItemClickCustom);

    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {
        int index = 1;
        if(!isStartPage) index = 2;
        if(position == mainPostList.size() - 1 && (mainPostList.size() - index) == MyConstance.ADS_LIMIT){
            holder.setNextItemData();
        }else if(position == 0 && !isStartPage){
            holder.setBackItemData();
        } else {
            holder.setData(mainPostList.get(position));
            setFavIfSelected(holder);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(mainPostList.get(position).getUid() == null){
            myViewType = 1;
        }else {
            myViewType = 0;
        }
        return myViewType;
    }

    @Override
    public int getItemCount() {

        return mainPostList.size();

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
        if(!isStartPage && listData.size() == MyConstance.ADS_LIMIT ||  adsButtonState == NEXT_ADS_BUTTON && !isStartPage) {
            mainPostList.add(new NewPost());
        }else if(!isStartPage && listData.size() < MyConstance.ADS_LIMIT && adsButtonState == BACK_ADS_BUTTON){

            loadFirstPage();

        }

        if(listData.size() == MyConstance.ADS_LIMIT) listData.add(new NewPost());
        mainPostList.addAll(listData);
        notifyDataSetChanged();
        adsButtonState = 0;

    }

    private void loadFirstPage(){
        dbManager.getDataFromDb(((MainActivity)context).currentCategory, "0");
        isStartPage = true;
    }

    public void setDbManager(DbManager dbManager){
        this.dbManager =  dbManager;
    }

    private void setFavIfSelected(AdsViewHolder holder){

        if(mainPostList.get(holder.getAdapterPosition()).isFav()){
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
        public ImageButton imFavorites;

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

        public void setNextItemData(){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbManager.getDataFromDb(((MainActivity) context).currentCategory, mainPostList.get(mainPostList.size() - 2).getTime());
                    ((MainActivity) context).rcView.scrollToPosition(0);
                    isStartPage = false;
                    adsButtonState = NEXT_ADS_BUTTON;
                }
            });
        }

        public void setBackItemData(){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbManager.getBackDataFromDb(((MainActivity) context).currentCategory, mainPostList.get(1).getTime());
                    ((MainActivity) context).rcView.scrollToPosition(0);
                    adsButtonState = BACK_ADS_BUTTON;
                }
            });
        }


        public void setData(NewPost newPost){

            if(newPost.getUid().equals(MainActivity.MAUTH)){

                editLayout.setVisibility(View.VISIBLE);

            }else{

                editLayout.setVisibility(View.GONE);

            }
            Picasso.get().load(newPost.getImId()).into(imItemView);
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

                    dbManager.updateFavorites(newPost, AdsViewHolder.this);

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

    public void clearAdapter(){

        mainPostList.clear();
        notifyDataSetChanged();

    }

    public List<NewPost> getMainList(){

        return mainPostList;
    }

}
