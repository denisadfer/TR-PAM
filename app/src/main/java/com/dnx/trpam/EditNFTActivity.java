package com.dnx.trpam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditNFTActivity extends AppCompatActivity {
    EditText descTxt;
    Button saveBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nftactivity);
        descTxt = findViewById(R.id.desc_edit);
        saveBtn = findViewById(R.id.save_edit_btn);
        String Nft_key = getIntent().getStringExtra("Nft_key");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Nft_Post");
        databaseReference.child(Nft_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                descTxt.setText(snapshot.child("description").getValue().toString());
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String desc = descTxt.getText().toString();
                        if (desc.isEmpty()){
                            descTxt.setError("Please, input the description");
                            descTxt.requestFocus();
                            return;
                        }
                        editDesc(desc);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editDesc(String desc) {
        String Nft_key = getIntent().getStringExtra("Nft_key");
        FirebaseDatabase.getInstance().getReference("Nft_Post").child(Nft_key).child("description").setValue(desc);

        progressDialog = new ProgressDialog(EditNFTActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                new AlertDialog.Builder(EditNFTActivity.this)
                        .setTitle("Description Changed")
                        .setMessage("Your nft description has been changed successfully")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }}).show();
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}