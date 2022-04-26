package com.dnx.trpam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditNFTActivity extends AppCompatActivity {
    EditText descTxt, tittletxt;
    Button saveBtn;
    ImageView backdtl;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nftactivity);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        descTxt = findViewById(R.id.desc_edit);
        tittletxt = findViewById(R.id.title_edit);
        saveBtn = findViewById(R.id.save_edit_btn);
        backdtl = findViewById(R.id.back_changedtl);
        String Nft_key = getIntent().getStringExtra("Nft_key");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Nft_Post");
        databaseReference.child(Nft_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                descTxt.setText(snapshot.child("description").getValue().toString());
                tittletxt.setText(snapshot.child("title").getValue().toString());
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String desc = descTxt.getText().toString();
                        String title = tittletxt.getText().toString();
                        if (desc.isEmpty()){
                            descTxt.setError(getResources().getString(R.string.addnft_about));
                            descTxt.requestFocus();
                            return;
                        }
                        if (title.isEmpty()){
                            tittletxt.setError(getResources().getString(R.string.addnft_name));
                            tittletxt.requestFocus();
                            return;
                        }
                        editDesc(desc, title);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backdtl.setOnClickListener(view -> {
            finish();
        });
    }

    private void editDesc(String desc, String title) {
        String Nft_key = getIntent().getStringExtra("Nft_key");
        String Nft_token = getIntent().getStringExtra("Nft_token");
        FirebaseDatabase.getInstance().getReference("Nft_Post").child(Nft_key).child("description").setValue(desc);
        FirebaseDatabase.getInstance().getReference("Nft_Post").child(Nft_key).child("title").setValue(title);

        String notif_edit = "You have change NFT ("+ title + ") details";
        String history_note = firebaseUser.getDisplayName() +" has change NFT details";
        String notif_edit_in = "Anda telah merubah NFT ("+ title +")";
        String history_note_in = firebaseUser.getDisplayName() +" telah merubah NFT";

        History history = new History(history_note, Nft_token);
        Notif notif_lister = new Notif(notif_edit, firebaseUser.getDisplayName(),"yes");
        History history_in = new History(history_note_in, Nft_token);
        Notif notif_lister_in = new Notif(notif_edit_in, firebaseUser.getDisplayName(),"yes");

        FirebaseDatabase.getInstance().getReference("Notif_ENG").push().setValue(notif_lister);
        FirebaseDatabase.getInstance().getReference("History_ENG").push().setValue(history);
        FirebaseDatabase.getInstance().getReference("Notif_IN").push().setValue(notif_lister_in);
        FirebaseDatabase.getInstance().getReference("History_IN").push().setValue(history_in);

        progressDialog = new ProgressDialog(EditNFTActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setMessage(getResources().getString(R.string.wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                new AlertDialog.Builder(EditNFTActivity.this)
                        .setTitle(getResources().getString(R.string.edit_dtl))
                        .setMessage(getResources().getString(R.string.edit_dtl2))
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