package com.example.change.foodorder;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.change.foodorder.Model.Food;
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

                Food food = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(food.getImage()).into(foodImg);
                collapsingToolbarLayout.setTitle(food.getName());
                foodDesc.setText(food.getDescription());
                foodPrice.setText(food.getPrice());
                foodName.setText(food.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
