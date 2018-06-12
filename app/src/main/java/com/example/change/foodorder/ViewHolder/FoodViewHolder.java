package com.example.change.foodorder.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodName,foodPrice;
    public ImageView foodImage,foodFav,foodAddToCart;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);
        foodName = itemView.findViewById(R.id.food_name);
        foodImage = itemView.findViewById(R.id.food_image);
        foodFav = itemView.findViewById(R.id.favFoodList);
        foodAddToCart = itemView.findViewById(R.id.addToCartFoodList);
        foodPrice = itemView.findViewById(R.id.food_pr);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
