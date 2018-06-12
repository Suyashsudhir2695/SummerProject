package com.example.change.foodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn;
    Button btnSignUp;
    TextView txtSlogan;
    DatabaseReference table_user;
    FirebaseDatabase database;
    SignInButton mSignInButton;
    GoogleSignInOptions mGSO;
    GoogleSignInClient mSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Simply Food");
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

        txtSlogan = findViewById(R.id.txtSlogan);
        btnSignIn = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        mSignInButton = findViewById(R.id.googleSignInButton);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mGSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mSignInClient = GoogleSignIn.getClient(this, mGSO);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/rm.ttf");
        txtSlogan.setTypeface(typeface);
        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        String user = Paper.book().read(Common.USER);
        String pass = Paper.book().read(Common.PWD);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        if (user != null && pass != null){
            if (!user.isEmpty() && !pass.isEmpty())
                loginAutomatically(user,pass);
        }
    }

    private void signInWithGoogle() {

    }

    private void loginAutomatically(final String phone, final String pass) {
        if (Common.isConnected(getBaseContext())) {
            //kuheagkjh


            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Hang On...");
            dialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dialog.dismiss();
                    User user = dataSnapshot.child(phone).getValue(User.class);
                    user.setPhone(phone);
                    if (dataSnapshot.child(phone).exists()) {
                        if (user.getPassword().equals(pass)) {
                            Common.currentUser = user;
                            startActivity(new Intent(MainActivity.this, Home.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Check Your Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "We Can\'t Find That User", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Couldn't Connect to Internet! " +
                    "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();

        }
    }
}
