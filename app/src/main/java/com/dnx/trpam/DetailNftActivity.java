package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class DetailNftActivity extends AppCompatActivity {

    TextView nft_owner, nft_price, nft_token, nft_title;
    Button nft_listing, nft_buy;
    ImageView nft_img;
    DatabaseReference dataref;
    private FirebaseUser firebaseUser;
    LinearLayout nft_linear;
    Dialog mdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nft);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mdialog = new Dialog(this);

        nft_owner = findViewById(R.id.detail_nft_owner);
        nft_price = findViewById(R.id.detail_nft_price);
        nft_token = findViewById(R.id.detail_nft_token);
        nft_title = findViewById(R.id.detail_nft_title);
        nft_img = findViewById(R.id.detail_nft_img);
        nft_listing = findViewById(R.id.detail_nft_listing);
        nft_buy = findViewById(R.id.detail_nft_buy);
        nft_linear = findViewById(R.id.detail_nft_linearBtn);
        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        NftPost nftAdd_price = new NftPost();

        String Nft_key = getIntent().getStringExtra("Nft_Post_key");

        dataref.child(Nft_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String owner = snapshot.child("owner").getValue().toString();
                    String title = snapshot.child("title").getValue().toString();
                    String price = snapshot.child("price").getValue().toString();
                    String token = snapshot.child("token").getValue().toString();
                    String img = snapshot.child("img").getValue().toString();

                    nftAdd_price.setImg(img);
                    nftAdd_price.setTitle(title);
                    nftAdd_price.setToken(token);
                    nftAdd_price.setOwner(owner);

                    Picasso.get().load(img).into(nft_img);
                    nft_owner.setText(owner);
                    nft_title.setText(title);
                    nft_price.setText(price);
                    nft_token.setText(token);
                    if (owner.equals(firebaseUser.getDisplayName())){
                        nft_listing.setVisibility(View.VISIBLE);
                        nft_linear.setVisibility(View.VISIBLE);
                        nft_buy.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nft_listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button close_popup;
                EditText popup_price;
                ProSwipeButton swipe_listing;
                mdialog.setContentView(R.layout.popup_listing);
                popup_price = mdialog.findViewById(R.id.popup_price);
                swipe_listing = mdialog.findViewById(R.id.swipe_listing);
                close_popup = mdialog.findViewById(R.id.close_popup_btn);
                mdialog.show();
                close_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mdialog.dismiss();
                    }
                });
                swipe_listing.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                    @Override
                    public void onSwipeConfirm() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipe_listing.showResultIcon(true);
                                nftAdd_price.setPrice(Double.parseDouble(popup_price.getText().toString()));
                                dataref.child(Nft_key).setValue(nftAdd_price).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mdialog.dismiss();
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        },1000);

                                    }
                                });

                            }
                        },2000);


                    }
                });

            }
        });

    }
}