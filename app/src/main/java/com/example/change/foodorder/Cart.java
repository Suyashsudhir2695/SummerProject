package com.example.change.foodorder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Model.FBResponse;
import com.example.change.foodorder.Model.Notification;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.Model.Sender;
import com.example.change.foodorder.Model.Token;
import com.example.change.foodorder.Remote.APIService;
import com.example.change.foodorder.ViewHolder.CartAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference request;
    TextView txtTotalPrice;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    EditText editCommentComment,editAddressComment;
    APIService mService;
    Place shippingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("Cart");

        mService = Common.getApiService();


        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtTotalPrice = findViewById(R.id.totalt);
        btnPlace = findViewById(R.id.btnPlaceOrder);
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    showAlert();

                else
                    Toast.makeText(Cart.this, "Cart is Empty", Toast.LENGTH_SHORT).show();


            }
        });

        loadFoodList();

    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
        builder.setTitle("One More Step!");
        builder.setMessage("Enter Shipping Address");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.order_address_comment,null);
        final PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        fragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter Shipping Address");
        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        fragment.setFilter(typeFilter);


        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("Error Places", status.getStatusMessage());

            }
        });
        editCommentComment = view.findViewById(R.id.editCommentComment);

        builder.setView(view);


        builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request requests = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        shippingAddress.getAddress().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        editCommentComment.getText().toString(),
                        cart

                );

                String orderNumber = String.valueOf(System.currentTimeMillis());
                request.child(orderNumber).setValue(requests);
                new Database(getBaseContext()).emptyCart();
                sendNotification(orderNumber);

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

    private void sendNotification(final String orderNumber) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    Token serverToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("Simply Food","We have a new order. Order Number #"+orderNumber);
                    Sender content = new Sender(serverToken.getToken(),notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<FBResponse>() {
                                @Override
                                public void onResponse(Call<FBResponse> call, Response<FBResponse> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {

                                            Toast.makeText(Cart.this, "Thank You for Shopping With Us. Your Oder Has Been Placed!", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Error Placing Your Order. Don't Worry We'll Try Again!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FBResponse> call, Throwable t) {

                                    Log.e("Error placing order",t.getMessage());

                                }

                            });





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int tPrice = 0;

        for (Order o : cart)
            tPrice += (Integer.parseInt(o.getPrice())) * (Integer.parseInt(o.getQuantity()));
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(tPrice));


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == Common.DELETE)
            deleteCartItem(item.getOrder());


        return true;
    }

    private void deleteCartItem(int order) {
        cart.remove(order);
        new Database(this).emptyCart();

        for (Order o : cart)
            new Database(this).addToCart(o);

        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        loadFoodList();
    }
}
