package com.example.change.foodorder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference request;
    TextView txtTotalPrice;
    Button btnpalce;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("Cart");


        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtTotalPrice = findViewById(R.id.totalt);
        btnpalce = findViewById(R.id.btnPlaceOrder);
        btnpalce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();


                // Toast.makeText(Cart.this, "Sjsfhgdsf", Toast.LENGTH_SHORT).show();
            }
        });

        loadFoodList();

    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
        builder.setTitle("One More Step!");
        builder.setMessage("Enter Shipping Address");

        final EditText editText = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        builder.setView(editText);
        builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request requests = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        editText.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart

                );
                request.child(String.valueOf(System.currentTimeMillis())).setValue(requests);
                new Database(getBaseContext()).emptyCart();
                Toast.makeText(Cart.this, "Thank You for Shopping With Us. Your Oder Has Been Placed!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();


    }

    private void loadFoodList() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        int tPrice = 0;

        for (Order o : cart)
            tPrice += (Integer.parseInt(o.getPrice())) * (Integer.parseInt(o.getQuantity()));
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(tPrice));


    }
}
