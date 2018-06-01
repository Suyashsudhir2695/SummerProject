package com.example.change.foodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Food;
import com.example.change.foodorder.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestions = new ArrayList<>();
    MaterialSearchBar searchBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        setTitle("Items");

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        recyclerView = findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        Log.i("Cat", categoryId);
        if (!categoryId.isEmpty() && categoryId != null) {
            loadFoodList(categoryId);
        }

        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Search Simply Food");
        //searchBar.setSpeechMode(false);
        loadSuggest();

        searchBar.setLastSuggestions(suggestions);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<>();
                for (String search : suggestions) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);


                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchStart(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void searchStart(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.foodImage);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });


            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestions.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.foodImage);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

    }
}
