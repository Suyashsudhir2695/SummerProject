package com.example.change.foodorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Category;
import com.example.change.foodorder.Model.Token;
import com.example.change.foodorder.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference category;
    TextView textViewName;

    RecyclerView recycle_menu;
    RecyclerView.LayoutManager layoutManager;

    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);


        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnected(Home.this)) {
                    loadMenu();
                } else {
                    Toast.makeText(Home.this, "Couldn't Connect to Internet! " +
                            "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnected(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(getBaseContext(), "Couldn't Connect to Internet! " +
                            "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        database = FirebaseDatabase.getInstance();
        category = database.getReference("category");

        Paper.init(this);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Cart.class));
            }
        });

        fab.setCount(new Database(Home.this).getCartCount());


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        textViewName = headerView.findViewById(R.id.textViewfull);
        textViewName.setText(Common.currentUser.getName());

        recycle_menu = findViewById(R.id.recyclerView);
        recycle_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycle_menu.setLayoutManager(layoutManager);


        updateToken(FirebaseInstanceId.getInstance().getToken());


    }

    private void updateToken(String fbToken) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token token = new Token(fbToken, false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class).build();

        textViewName.setText(Common.currentUser.getName());
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.textMenu.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get the CategoryID/MenuId
                        Intent foodIntent = new Intent(Home.this, FoodList.class);
                        foodIntent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodIntent);
                    }
                });

            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycle_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(Home.this, Cart.class));

        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(Home.this,OrderStatus.class);
            intent.putExtra("userPhone",Common.currentUser.getPhone());
            Log.i("UserPhone" ,Common.currentUser.getPhone());
            startActivity(intent);

        } else if (id == R.id.nav_pass) {
            changePassword();

        } else if (id == R.id.nav_acc) {

            Paper.book().destroy();

            Intent signOut = new Intent(Home.this, MainActivity.class);
            signOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signOut);
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Change Your Password");
        builder.setMessage("Type in the old password and the the new one!");
        builder.setIcon(R.drawable.ic_security_black_24dp);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_password_layout, null);
        final MaterialEditText editOldPass = view.findViewById(R.id.editOldPass);
        final MaterialEditText editNewPass = view.findViewById(R.id.editNewPass);
        final MaterialEditText editPassAgain = view.findViewById(R.id.editPassAgain);
        builder.setView(view);

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                if (editOldPass.getText().toString().equals(Common.currentUser.getPassword())) {
                    if (editNewPass.getText().toString().equals(editPassAgain.getText().toString())) {
                        Map<String, Object> updatedPass = new HashMap<>();
                        updatedPass.put("password", editNewPass.getText().toString());

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user");
                        user.child(Common.currentUser.getPhone()).updateChildren(updatedPass)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, "Your Password's Been Updated", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Check Your Old Password Again", Toast.LENGTH_SHORT).show();
                }

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fab.setCount(new Database(Home.this).getCartCount());
    }
}
