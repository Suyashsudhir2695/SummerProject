package com.example.change.foodorder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText editPhone;
    EditText editPass;
    Button btnSignIn;
    DatabaseReference table_user;
    FirebaseDatabase database;
    CheckBox checkBox;
    TextView forgotPass;
    EditText forgotPhoneEdit;
    EditText secureCodeEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("Sign In");
        editPhone = findViewById(R.id.editPhone);
        editPass = findViewById(R.id.editPass);
        btnSignIn = findViewById(R.id.btnLoginS);
        checkBox = findViewById(R.id.remember);
        forgotPass = findViewById(R.id.txtPassForgot);

        Paper.init(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPass();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnected(getBaseContext())) {


                    if (checkBox.isChecked()){
                        Paper.book().write(Common.USER,editPhone.getText().toString());
                        Paper.book().write(Common.PWD,editPass.getText().toString());
                    }

                    final String phone = editPhone.getText().toString();
                    final String pass = editPass.getText().toString();
                    final ProgressDialog dialog = new ProgressDialog(SignIn.this);
                    dialog.setMessage("Hang On...");
                    dialog.show();


                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
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


                                    table_user.removeEventListener(this);

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
                else {
                    Toast.makeText(SignIn.this, "Couldn't Connect to Internet! " +
                            "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void showForgotPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Did You Forget Your Password?");
        builder.setMessage("Fill In Your Information and Leave the rest to Us");
        builder.setIcon(R.drawable.ic_security_black_24dp);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_password_layout,null);
        forgotPhoneEdit = view.findViewById(R.id.editPhoneForgot);
        secureCodeEdit = view.findViewById(R.id.editCodeForgot);
        builder.setView(view);


        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (forgotPhoneEdit.getText().toString().isEmpty()) {
                    Toast.makeText(SignIn.this, "Fill in the Phone", Toast.LENGTH_SHORT).show();
                    return;

                } else if (secureCodeEdit.getText().toString().isEmpty()) {
                    Toast.makeText(SignIn.this, "Fill In The Secure Code", Toast.LENGTH_SHORT).show();
                    return;

                } else{


                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.child(forgotPhoneEdit.getText().toString()).getValue(User.class);

                            if (user.getSecureCode().equals(secureCodeEdit.getText().toString()))
                                Toast.makeText(SignIn.this, "Your Password is " + user.getPassword(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignIn.this, "Invalid Secure Code", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
}
