package com.example.change.foodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    EditText editPhone;
    EditText editPass;
    Button btnSignIn;
    DatabaseReference table_user;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("Sign In");
        editPhone = findViewById(R.id.editPhone);
        editPass = findViewById(R.id.editPass);
        btnSignIn = findViewById(R.id.btnLoginS);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = editPhone.getText().toString();
                final String pass = editPass.getText().toString();
                final ProgressDialog dialog = new ProgressDialog(SignIn.this);
                dialog.setMessage("Hang On...");
                dialog.show();
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(editPhone.getText().toString());
                        if (dataSnapshot.child(phone).exists()) {
                            if (user.getPassword().equals(pass)) {
                                Common.currentUser = user;
                                startActivity(new Intent(SignIn.this, Home.class));
                                finish();
                            } else {
                                Toast.makeText(SignIn.this, "Check Your Credentials", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignIn.this, "We Can\'t Find That User", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
