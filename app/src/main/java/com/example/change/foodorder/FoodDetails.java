package com.example.change.foodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Model.Food;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetails extends AppCompatActivity implements RatingDialogListener{

    TextView foodName, foodPrice, foodDesc;
    ImageView foodImg;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;


    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foods;
    Food currentFood;
    DatabaseReference ratingTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        setTitle("Details");


        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTable = database.getReference("Ratings");

        btnCart = findViewById(R.id.btnCart);
        foodDesc = findViewById(R.id.foodDesc);
        foodName = findViewById(R.id.food_name);
        foodPrice = findViewById(R.id.food_price);
        foodImg = findViewById(R.id.img_food);
        btnRating = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.ratingBar);

        numberButton = findViewById(R.id.numberButton);

        collapsingToolbarLayout = findViewById(R.id.collapsing);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandingAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsingAppBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();

            }
        });



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

        btnCart.setCount(new Database(this).getCartCount());


        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");

        if (!foodId.isEmpty()) {
            if (Common.isConnected(getBaseContext())) {
                getFoodDetails(foodId);
                getFoodRating(foodId);
            }

            else {
                Toast.makeText(FoodDetails.this, "Couldn't Connect to Internet! " +
                        "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

        }


    }

    private void getFoodRating(String foodId) {
        Query rating = ratingTable.orderByChild("foodId").equalTo(foodId);

        rating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShop:dataSnapshot.getChildren()){
                    Rating item = postSnapShop.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;

                }
                if (count != 0) {
                    float avg = (float) (sum / count);
                    ratingBar.setRating(avg);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","OK","Very Good","Loved It"))
                .setDefaultRating(1)
                .setTitle("Rate this Dish")
                .setDescription("Please Give Your Valuable Feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("How Can We Improve It Further?")
                .setHintTextColor(R.color.colorPrimary)
                .setCommentTextColor(android.R.color.black)
                .setCommentBackgroundColor(android.R.color.white)
                .setWindowAnimation(R.style.RatingFadeAnim)
                .create(FoodDetails.this).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refreshFoodDetails) {
            getFoodDetails(foodId);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveButtonClicked(int value, String comments) {

        //Record the value of rating

        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);

        ratingTable.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()){
                    ratingTable.child(Common.currentUser.getPhone()).removeValue();

                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else {
                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);

                }
                Toast.makeText(FoodDetails.this, "Thank You For Your Feedback", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onNegativeButtonClicked() {

    }


}
