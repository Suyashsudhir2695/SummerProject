package com.example.change.foodorder.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.change.foodorder.Cart;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView text_cart_item_name, text_cart_item_price;
    public ElegantNumberButton cart_item_count;

    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);

        text_cart_item_name = itemView.findViewById(R.id.text_cart_item_name);
        text_cart_item_price = itemView.findViewById(R.id.text_cart_item_price);
        cart_item_count = itemView.findViewById(R.id.numberButtonCart);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setText_cart_item_name(TextView text_cart_item_name) {
        this.text_cart_item_name = text_cart_item_name;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an Action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);


    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {


    private List<Order> listData = new ArrayList<>();
    private Cart context;

    public CartAdapter(List<Order> listData, Cart context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {

        holder.cart_item_count.setNumber(listData.get(position).getQuantity());

        holder.cart_item_count.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(context).updateCart(order);
            }
        });




        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

        holder.text_cart_item_price.setText(fmt.format(price));
        holder.text_cart_item_name.setText(listData.get(position).getProductName());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
