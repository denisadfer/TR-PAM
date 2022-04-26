package com.dnx.trpam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView backt;
    ProgressDialog progressDialog;
    double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        nominal = findViewById(R.id.nominal);
        btn_topup = findViewById(R.id.btn_topup);
        backt = findViewById(R.id.back_topup);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataUser");

        btn_topup.setOnClickListener(view -> {
            if (TextUtils.isEmpty(nominal.getText().toString())){
                nominal.setError(getResources().getString(R.string.topup_failed));
                nominal.requestFocus();
                return;
            }
            if (Double.parseDouble(nominal.getText().toString())>0){
                databaseReference.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){



                            total = 0;
                            double balance = Double.parseDouble(snapshot.child("balance").getValue().toString());
                            double topUp = Double.parseDouble(nominal.getText().toString());
                            total = balance + topUp;

                            update(total);
                            progressDialog = new ProgressDialog(TopUp.this);
                            progressDialog.setTitle(getResources().getString(R.string.loading));
                            progressDialog.setMessage(getResources().getString(R.string.wait));
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(TopUp.this)
                                        .setTitle(getResources().getString(R.string.topupsuccess))
                                        .setMessage(getResources().getString(R.string.welcome) + total + " ETH")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                finish();
                                            }}).show();
                            }, 1000);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                nominal.setError(getResources().getString(R.string.topup_failed2));
                nominal.requestFocus();
            }

        });

        backt.setOnClickListener(view -> finish());
    }

    private void update(double total) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("DataUser").child(firebaseUser.getDisplayName()).child("balance").setValue(total);
    }
}