package com.example.change.foodorder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Food;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> qadapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //FireBase Init

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
       // loadOrders(Common.currentUser.getPhone());

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent() == null) {
            loadOrders(Common.currentUser.getPhone());
        } else {
            loadOrders(getIntent().getStringExtra("userPhone"));
        }


    }

    private void loadOrders( String phone) {
        Query query = requests.orderByChild("phone").equalTo(phone);

        FirebaseRecyclerOptions<Request> options  = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests.orderByChild("phone").equalTo(phone),Request.class).build();
        qadapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.txtOrderId.setText(qadapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.codeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });




            }


            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.order_status_layout,parent,false);


                return new OrderViewHolder(itemView);
            }
        };
        qadapter.startListening();
        recyclerView.setAdapter(qadapter);
    }


}
