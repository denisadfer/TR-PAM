package com.dnx.trpam;

import android.os.Bundle;
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
        finish();
    }
}