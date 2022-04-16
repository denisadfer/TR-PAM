package com.dnx.trpam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button btnLogout;
    TextView TxtName;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.logout_btn);
        TxtName = findViewById(R.id.textName);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!= null) {
            TxtName.setText("Welcome, "+firebaseUser.getDisplayName());
        } else {
            TxtName.setText("Data Name is empty");
        }

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }
}