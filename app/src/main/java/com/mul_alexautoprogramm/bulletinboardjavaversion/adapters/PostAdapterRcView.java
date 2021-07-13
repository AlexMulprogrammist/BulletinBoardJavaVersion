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

import com.mul_alexautoprogramm.bulletinboardjavaversion.DbManager;
import com.mul_alexautoprogramm.bulletinboardjavaversion.EditActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.MainActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.NewPost;
import com.mul_alexautoprogramm.bulletinboardjavaversion.R;
import com.mul_alexautoprogramm.bulletinboardjavaversion.ShowLayoutActivity;
import com.mul_alexautoprogramm.bulletinboardjavaversion.utils.MyConstance;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapterRcView extends RecyclerView.Adapter<PostAdapterRcView.AdsViewHolder> {
    private List<NewPost> arrayListPost;
    private Context context;
    private onItemClickCustom onItemClickCustom;
    private DbManager dbManager;


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
        private TextView tvItemTitle,tvItemPrice,tvItemTelNumb,tvItemDescription, tvTotal_views;
        private ImageView imItemView;
        private LinearLayout editLayout;
        private onItemClickCustom onItemClickCustom;
        private ImageButton imEditItem;
        private ImageButton imDeleteItem;



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
                    i.putExtra(MyConstance.IMAGE_ID, newPost.getImId());
                    i.putExtra(MyConstance.IMAGE_ID_2, newPost.getImId2());
                    i.putExtra(MyConstance.IMAGE_ID_3, newPost.getImId3());
                    i.putExtra(MyConstance.TITLE, newPost.getTitle());
                    i.putExtra(MyConstance.PRICE, newPost.getPrice());
                    i.putExtra(MyConstance.TEL_NUMB, newPost.getTel_numb());
                    i.putExtra(MyConstance.DESC, newPost.getDesc());
                    i.putExtra(MyConstance.KEY, newPost.getKey());
                    i.putExtra(MyConstance.UID, newPost.getUid());
                    i.putExtra(MyConstance.TIME, newPost.getTime());
                    i.putExtra(MyConstance.CATEGORY, newPost.getCategory());
                    i.putExtra(MyConstance.EDIT_STATE, true);
                    i.putExtra(MyConstance.TOTAL_VIEWS, newPost.getTotalViews());
                    context.startActivity(i);

                }
            });

        }



        @Override
        public void onClick(View v) {

            NewPost newPost = arrayListPost.get(getAdapterPosition());
            dbManager.updateTotalViews(newPost);
            Intent i = new Intent(context, ShowLayoutActivity.class);

            i.putExtra(MyConstance.IMAGE_ID, newPost.getImId());
            i.putExtra(MyConstance.IMAGE_ID_2, newPost.getImId2());
            i.putExtra(MyConstance.IMAGE_ID_3, newPost.getImId3());

            i.putExtra(MyConstance.TITLE, newPost.getTitle());
            i.putExtra(MyConstance.PRICE, newPost.getPrice());
            i.putExtra(MyConstance.TEL_NUMB, newPost.getTel_numb());
            i.putExtra(MyConstance.DESC, newPost.getDesc());

            context.startActivity(i);

            onItemClickCustom.onItemSelected(getAdapterPosition());

        }
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
                arrayListPost.remove(position);
                notifyItemRemoved(position);

            }
        });



        builder.show();

    }


    public interface onItemClickCustom{

        void onItemSelected(int position);

    }

    public void updateAdapter(List<NewPost> listData){

        arrayListPost.clear();
        arrayListPost.addAll(listData);
        notifyDataSetChanged();

    }

    public void setDbManager(DbManager dbManager){

        this.dbManager =  dbManager;

    }

}
