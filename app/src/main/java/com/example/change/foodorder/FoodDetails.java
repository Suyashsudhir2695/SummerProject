package com.example.change.foodorder;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Model.Food;
import com.example.change.foodorder.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {

    TextView foodName, foodPrice, foodDesc;
    ImageView foodImg;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    Food currentFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        setTitle("Details");


        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        btnCart = findViewById(R.id.btnCart);
        foodDesc = findViewById(R.id.foodDesc);
        foodName = findViewById(R.id.food_name);
        foodPrice = findViewById(R.id.food_price);
        foodImg = findViewById(R.id.img_food);

        numberButton = findViewById(R.id.numberButton);

        collapsingToolbarLayout = findViewById(R.id.collapsing);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandingAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsingAppBar);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount()

                        )


                );
                Toast.makeText(FoodDetails.this, currentFood.getName() + " has been added", Toast.LENGTH_SHORT).show();
            }
        });


        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");

        if (!foodId.isEmpty()) {
            getFoodDetails(foodId);

        }


    }

    private void getFoodDetails(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(foodImg);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                foodDesc.setText(currentFood.getDescription());
                foodPrice.setText(currentFood.getPrice());
                foodName.setText(currentFood.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
