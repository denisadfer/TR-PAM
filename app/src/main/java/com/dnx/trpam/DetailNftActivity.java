package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.HashMap;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class DetailNftActivity extends AppCompatActivity {

    TextView nft_owner, nft_price, nft_token, nft_title, history_nodata;
    Button nft_listing, nft_buy;
    ImageView nft_img;
    DatabaseReference dataref, dataref2, dataref3;
    private FirebaseUser firebaseUser;
    LinearLayout nft_linear;
    Dialog mdialog;
    RecyclerView history_recycler;
    FirebaseRecyclerOptions<History> nft_options;
    FirebaseRecyclerAdapter<History,HistoryViewHolder> adapter;
    Query datarefQ;
    double saldo, harga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nft);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mdialog = new Dialog(this);

        history_recycler = findViewById(R.id.detail_recycler);
        nft_owner = findViewById(R.id.detail_nft_owner);
        nft_price = findViewById(R.id.detail_nft_price);
        nft_token = findViewById(R.id.detail_nft_token);
        nft_title = findViewById(R.id.detail_nft_title);
        nft_img = findViewById(R.id.detail_nft_img);
        nft_listing = findViewById(R.id.detail_nft_listing);
        nft_buy = findViewById(R.id.detail_nft_buy);
        nft_linear = findViewById(R.id.detail_nft_linearBtn);
        history_nodata = findViewById(R.id.history_nodata);
        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        dataref2 = FirebaseDatabase.getInstance().getReference().child("History");
        dataref3 = FirebaseDatabase.getInstance().getReference().child("DataUser");

        NftPost nftAdd_price = new NftPost();
        NftPost nftAdd_buy = new NftPost();

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
                    nftAdd_buy.setImg(img);
                    nftAdd_buy.setTitle(title);
                    nftAdd_buy.setToken(token);
                    nftAdd_buy.setOwner(firebaseUser.getDisplayName());
                    nftAdd_buy.setPrice(0);

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
                    datarefQ = dataref2.orderByChild("token").equalTo(token);
                    history_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    history_recycler.setHasFixedSize(true);
                    LoadHistory();

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
                ImageView popup_img;
                mdialog.setContentView(R.layout.popup_listing);
                popup_price = mdialog.findViewById(R.id.popup_price);
                swipe_listing = mdialog.findViewById(R.id.swipe_listing);
                close_popup = mdialog.findViewById(R.id.close_popup_btn);
                popup_img = mdialog.findViewById(R.id.popup_img_nft);
                Picasso.get().load(nftAdd_price.getImg()).into(popup_img);
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
                                String buyer = "";
                                String aksi = "Listing NFT for";
                                History history = new History(nftAdd_price.owner, buyer,popup_price.getText().toString(),aksi, nftAdd_price.token);
                                dataref2.push().setValue(history);
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
        nft_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button close_popup;
                TextInputLayout popup_balance_lay;
                EditText popup_price,popup_balance;
                ProSwipeButton swipe_buy;
                ImageView popup_img;
                mdialog.setContentView(R.layout.popup_buy);
                popup_price = mdialog.findViewById(R.id.popup_price_buy);
                popup_balance = mdialog.findViewById(R.id.popup_balance);
                popup_balance_lay = mdialog.findViewById(R.id.textinput_balance);
                swipe_buy = mdialog.findViewById(R.id.swipe_buy);
                close_popup = mdialog.findViewById(R.id.close_popup_btn_buy);
                popup_img = mdialog.findViewById(R.id.popup_img_nft_buy);
                Picasso.get().load(nftAdd_price.getImg()).into(popup_img);


                mdialog.show();

                dataref3.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            popup_balance.setText("Your Balance : " + snapshot.child("balance").getValue().toString() + " ETH");
                            saldo = Double.parseDouble(snapshot.child("balance").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                popup_price.setText("Price : "+ nft_price.getText().toString());

                harga = Double.parseDouble(nft_price.getText().toString());

                close_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mdialog.dismiss();
                    }
                });
                swipe_buy.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                    @Override
                    public void onSwipeConfirm() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (harga > saldo){
                                    swipe_buy.showResultIcon(false);
                                    popup_balance_lay.setError("Your balance is kurang please topup");

                                } else {
                                    Double saldo_akhir = saldo-harga;
                                    swipe_buy.showResultIcon(true);
                                    String buyer = firebaseUser.getDisplayName();
                                    String aksi = "Tranfer Nft to";
                                    History history = new History(nft_owner.getText().toString(), buyer,nft_price.getText().toString(),aksi, nftAdd_buy.token);
                                    dataref2.push().setValue(history);
                                    dataref3.child(firebaseUser.getDisplayName()).child("balance").setValue(saldo_akhir);
                                    dataref.child(Nft_key).setValue(nftAdd_buy).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                            }
                        },2000);
                    }
                });
            }
        });


    }

    private void LoadHistory() {
        nft_options = new FirebaseRecyclerOptions.Builder<History>().setQuery(datarefQ,History.class).build();
        adapter = new FirebaseRecyclerAdapter<History, HistoryViewHolder>(nft_options) {
            // Add this
            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)
                    history_nodata.setVisibility(View.VISIBLE);
            }
            @Override
            protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull History model) {
                holder.owner.setText(model.getOwner());
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                holder.dateH.setText(dateFormat.format(model.dateBuy));
                holder.action.setText(model.getAksi());
                if (model.getBuyer() != null){
                    holder.newOwner.setText(model.getBuyer());
                } else {
                    holder.newOwner.setVisibility(View.GONE);
                }
                if (model.getPrice().equals("0")){
                    holder.price.setVisibility(View.GONE);

                } else {
                    holder.price.setText(model.getPrice());
                }
            }

            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_history,parent,false);
                return new HistoryViewHolder(view);
            }
        };
        adapter.startListening();
        history_recycler.setAdapter(adapter);
    }

//        @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
}