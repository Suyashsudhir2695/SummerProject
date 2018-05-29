package com.example.change.foodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.change.foodorder.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {
    Button btnSignUp;
    DatabaseReference table_user;
    FirebaseDatabase database;
    private EditText mEditTextPass, mEditTextPhone, mEditTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        mEditTextName = (MaterialEditText) findViewById(R.id.editNameSignUp);
        mEditTextPass = (MaterialEditText) findViewById(R.id.editPassSignUp);
        mEditTextPhone = (MaterialEditText) findViewById(R.id.editPhoneSignUp);

        Button mBtnSignIn = findViewById(R.id.btnLoginSignUp);


        //Initialize firebase

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("user");


        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = mEditTextPhone.getText().toString();
                final String pass = mEditTextPass.getText().toString();
                final String name = mEditTextName.getText().toString();

                final ProgressDialog mProgressDialog = new ProgressDialog(SignUp.this);
                mProgressDialog.setMessage("Hang On! Signing You Up ...");
                mProgressDialog.show();
                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(phone).exists()) {

                            Toast.makeText(SignUp.this, "This Phone Exists Already", Toast.LENGTH_SHORT).show();
                        } else {

                            User mUser = new User(name, pass);
                            mDatabaseReference.child(phone).setValue(mUser);
                            Toast.makeText(SignUp.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(SignUp.this, SignIn.class));
                        }
                        mProgressDialog.dismiss();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
