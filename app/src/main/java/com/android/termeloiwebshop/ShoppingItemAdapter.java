package com.android.termeloiwebshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG = MainActivity.class.getName();

    public ShoppingItem clickedItem;

    private ArrayList<ShoppingItem> shoppingItemData;
    private ArrayList<ShoppingItem> shoppingItemsDataAll;
    private Context shopContext;
    private int lastPostition = -1;

    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemData){
        this.shoppingItemData = itemData;
        this.shoppingItemsDataAll =itemData;
        this.shopContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(shopContext).inflate(R.layout.shop_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder( ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = shoppingItemData.get(position);

        holder.binder(currentItem);

        if(holder.getAdapterPosition() > lastPostition){
            Animation animation = AnimationUtils.loadAnimation(shopContext,R.anim.shopitemanim);
            holder.itemView.startAnimation(animation);
            lastPostition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return shoppingItemData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults res = new FilterResults();

            if(constraint == null || constraint.length() == 0){
                res.count = shoppingItemsDataAll.size();
                res.values = shoppingItemsDataAll;
            }else {
                String filterP = constraint.toString().toLowerCase().trim();

                for(ShoppingItem item : shoppingItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterP)){
                        filteredList.add(item);
                    }
                }
                res.count =filteredList.size();
                res.values = filteredList;
            }
            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shoppingItemData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class  ViewHolder extends  RecyclerView.ViewHolder{
        private ImageView itemImage;
        private RatingBar ratingBar;
        private TextView itemTitle;
        private TextView itemInfo;
        private TextView itemPrice;

        public ViewHolder( View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.shopItemImage);
            ratingBar = itemView.findViewById(R.id.shopRatingBar);
            itemTitle = itemView.findViewById(R.id.shopItemTitle);
            itemInfo = itemView.findViewById(R.id.shotItemInfo);
            itemPrice = itemView.findViewById(R.id.shopItemPrice);

            String name = itemTitle.getText().toString();
            String info = itemInfo.getText().toString();
            String price = itemPrice.getText().toString();
            int ratingInfo = ratingBar.getNumStars();
            int imageRes = itemImage.getId();



            itemView.findViewById(R.id.shopItemAddCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Termék kosárba rakása");

                    clickedItem =  new ShoppingItem(name,  info,  price,  ratingInfo, imageRes);
                    //Item =  new ShoppingItem(name,  info,  price,  ratingInfo, imageRes);
                    //((ShopActivity)shopContext).addItem(clickedItem);
                    Log.d(LOG_TAG, " Termék:"  + clickedItem.getName());
                    ((ShopActivity)shopContext).updateAlertIcon();
                }
            });

        }

        public void binder(ShoppingItem currentItem) {

            ratingBar.setRating(currentItem.getRatingInfo());
            itemTitle.setText(currentItem.getName());
            itemInfo.setText(currentItem.getInfo());
            itemPrice.setText(currentItem.getPrice());

            Glide.with(shopContext).load(currentItem.getImageRes()).into(itemImage);
        }
    }
/*
    public ShoppingItem kosarba(String name, String info, String price, float ratingInfo, int imageRes){
        ShoppingItem item = new ShoppingItem( name,  info,  price,  ratingInfo, imageRes);
        return item;
    }
*/
}

