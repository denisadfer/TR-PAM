package com.dnx.trpam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TopUp extends AppCompatActivity {
    EditText nominal;
    Button btn_topup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        nominal = findViewById(R.id.nominal);
        btn_topup = findViewById(R.id.btn_topup);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataUser");

        databaseReference.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    btn_topup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            double total = 0;
                            double balance = Double.parseDouble(snapshot.child("balance").getValue().toString());
                            double topUp = Double.parseDouble(nominal.getText().toString());
                            total = balance + topUp;

                            update(total);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(double total) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("DataUser").child(firebaseUser.getDisplayName()).child("balance").setValue(total);
    }
}