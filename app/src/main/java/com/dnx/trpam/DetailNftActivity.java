package com.dnx.trpam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class DetailNftActivity extends AppCompatActivity {

    TextView nft_owner, nft_price, nft_token, nft_title, nft_creator, nft_desc, nft_owner_fn, nft_owner_telp,history_nodata, nft_price_not;
    Button nft_listing, nft_buy, nft_edit, nft_delete, nft_download, nft_unlisting;
    ImageView nft_img, arrow_button1, arrow_button2, arrow_button3, arrow_button4, arrow_button5, backd;
    CardView base_card1, base_card2, base_card3, base_card4, base_card5;
    DatabaseReference dataref, dataref2a, dataref3, dataref4a, dataref2b, dataref4b;
    StorageReference storageref;
    private FirebaseUser firebaseUser;
    LinearLayout nft_linear, hidden_view1, hidden_view2, hidden_view3, hidden_view4, hidden_view5;
    Dialog mdialog;
    RecyclerView history_recycler;
    private ProgressDialog progressDialog;
    FirebaseRecyclerOptions<History> nft_options;
    FirebaseRecyclerAdapter<History,HistoryViewHolder> adapter;
    Query datarefQa, datarefQb;
    double saldo, harga;
    double saldo_penjual_nft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nft);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mdialog = new Dialog(this);

        history_recycler = findViewById(R.id.detail_recycler);
        nft_owner = findViewById(R.id.detail_nft_owner);
        nft_owner_fn = findViewById(R.id.detail_nft_owner_name);
        nft_owner_telp = findViewById(R.id.detail_nft_owner_telp);
        nft_creator = findViewById(R.id.detail_nft_creator);
        nft_desc = findViewById(R.id.detail_nft_deskripsi);
        nft_price = findViewById(R.id.detail_nft_price);
        nft_price_not = findViewById(R.id.detail_nft_notlisted);
        nft_token = findViewById(R.id.detail_nft_token);
        nft_title = findViewById(R.id.detail_nft_title);
        nft_img = findViewById(R.id.detail_nft_img);
        nft_listing = findViewById(R.id.detail_nft_listing);
        nft_unlisting = findViewById(R.id.detail_nft_unlisting);
        nft_buy = findViewById(R.id.detail_nft_buy);
        nft_edit = findViewById(R.id.detail_nft_edit);
        nft_delete = findViewById(R.id.detail_nft_delete);
        nft_download = findViewById(R.id.detail_nft_download);
        nft_linear = findViewById(R.id.detail_nft_linearBtn);
        history_nodata = findViewById(R.id.history_nodata);
        base_card1 = findViewById(R.id.base_cardview1);
        base_card2 = findViewById(R.id.base_cardview2);
        base_card3 = findViewById(R.id.base_cardview3);
        base_card4 = findViewById(R.id.base_cardview4);
        base_card5 = findViewById(R.id.base_cardview5);
        arrow_button1 = findViewById(R.id.arrow_button1);
        arrow_button2 = findViewById(R.id.arrow_button2);
        arrow_button3 = findViewById(R.id.arrow_button3);
        arrow_button4 = findViewById(R.id.arrow_button4);
        arrow_button5 = findViewById(R.id.arrow_button5);
        hidden_view1 = findViewById(R.id.hidden_view1);
        hidden_view2 = findViewById(R.id.hidden_view2);
        hidden_view3 = findViewById(R.id.hidden_view3);
        hidden_view4 = findViewById(R.id.hidden_view4);
        hidden_view5 = findViewById(R.id.hidden_view5);
        backd = findViewById(R.id.back_detail);

        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        dataref2a = FirebaseDatabase.getInstance().getReference().child("History_ENG");
        dataref2b = FirebaseDatabase.getInstance().getReference().child("History_IN");
        dataref3 = FirebaseDatabase.getInstance().getReference().child("DataUser");
        dataref4a = FirebaseDatabase.getInstance().getReference().child("Notif_ENG");
        dataref4b = FirebaseDatabase.getInstance().getReference().child("Notif_IN");


        NftPost nftAdd_price = new NftPost();
        NftPost nftAdd_buy = new NftPost();

        String Nft_key = getIntent().getStringExtra("Nft_Post_key");

        dataref.child(Nft_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String owner = snapshot.child("owner").getValue().toString();
                    String creator = snapshot.child("creator").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();
                    String title = snapshot.child("title").getValue().toString();
                    String price = snapshot.child("price").getValue().toString();
                    String token = snapshot.child("token").getValue().toString();
                    String img = snapshot.child("img").getValue().toString();

                    dataref3.child(owner).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String owner_fn = snapshot.child("name").getValue().toString();
                            String owner_telp = snapshot.child("phone").getValue().toString();
                            nft_owner_fn.setText(owner_fn);
                            nft_owner_telp.setText(owner_telp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    nft_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(DetailNftActivity.this, NFTPreview.class);
                            intent.putExtra("img", img);
                            startActivity(intent);
                        }
                    });

                    storageref = FirebaseStorage.getInstance().getReferenceFromUrl(img);

                    nftAdd_price.setImg(img);
                    nftAdd_price.setTitle(title);
                    nftAdd_price.setCreator(creator);
                    nftAdd_price.setDescription(description);
                    nftAdd_price.setToken(token);
                    nftAdd_price.setOwner(owner);
                    nftAdd_buy.setImg(img);
                    nftAdd_buy.setTitle(title);
                    nftAdd_buy.setCreator(creator);
                    nftAdd_buy.setDescription(description);
                    nftAdd_buy.setToken(token);
                    nftAdd_buy.setOwner(firebaseUser.getDisplayName());
                    nftAdd_buy.setPrice(0);

                    Picasso.get().load(img).into(nft_img);
                    nft_owner.setText(owner);
                    nft_title.setText(title);
                    nft_creator.setText(creator);
                    nft_desc.setText(description);
                    if (price.equals("0")){
                        nft_price.setText(price);
                        nft_price_not.setVisibility(View.VISIBLE);
                        nft_price.setVisibility(View.GONE);
                    } else {
                        nft_price.setText(price);
                    }

                    nft_token.setText(token);
                    if (nft_price.getText().toString().equals("0")){
                        if (owner.equals(firebaseUser.getDisplayName())){
                            base_card1.setVisibility(View.VISIBLE);
                            nft_linear.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (owner.equals(firebaseUser.getDisplayName())){
                            base_card1.setVisibility(View.VISIBLE);
                            nft_linear.setVisibility(View.VISIBLE);
                            nft_unlisting.setVisibility(View.VISIBLE);
                        } else {
                            nft_buy.setVisibility(View.VISIBLE);
                        }
                    }

                    datarefQa = dataref2a.orderByChild("token").equalTo(token);
                    datarefQb = dataref2b.orderByChild("token").equalTo(token);
                    history_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    //  karna gamuncul kalo pake ini
                    //  history_recycler.setHasFixedSize(true);
                    SharedPreferences shp = getSharedPreferences(
                            "Settings",Context.MODE_PRIVATE);
                    String language = shp.getString("My_Lang","");

                    if (language.equals("in")){
                        LoadHistory(datarefQb);
                    } else {
                        LoadHistory(datarefQa);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backd.setOnClickListener(view -> {
            finish();
        });

        //fungsi liat liat
        base_card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidden_view1.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(base_card1,
                            new AutoTransition());
                    hidden_view1.setVisibility(View.GONE);
                    arrow_button1.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(base_card1,
                            new AutoTransition());
                    hidden_view1.setVisibility(View.VISIBLE);
                    arrow_button1.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
        base_card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidden_view2.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(base_card2,
                            new AutoTransition());
                    hidden_view2.setVisibility(View.GONE);
                    arrow_button2.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(base_card2,
                            new AutoTransition());
                    hidden_view2.setVisibility(View.VISIBLE);
                    arrow_button2.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
        base_card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidden_view3.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(base_card3,
                            new AutoTransition());
                    hidden_view3.setVisibility(View.GONE);
                    arrow_button3.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(base_card3,
                            new AutoTransition());
                    hidden_view3.setVisibility(View.VISIBLE);
                    arrow_button3.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
        base_card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidden_view4.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(base_card4,
                            new AutoTransition());
                    hidden_view4.setVisibility(View.GONE);
                    arrow_button4.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(base_card4,
                            new AutoTransition());
                    hidden_view4.setVisibility(View.VISIBLE);
                    arrow_button4.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
        base_card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidden_view5.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(base_card5,
                            new AutoTransition());
                    hidden_view5.setVisibility(View.GONE);
                    arrow_button5.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(base_card5,
                            new AutoTransition());
                    hidden_view5.setVisibility(View.VISIBLE);
                    arrow_button5.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });

        //fungsi inti nft
        nft_listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button close_popup;
                EditText popup_price;
                ProSwipeButton swipe_listing;
                TextInputLayout popup_listing_lay;
                ImageView popup_img;
                mdialog.setContentView(R.layout.popup_listing);
                popup_price = mdialog.findViewById(R.id.popup_price);
                swipe_listing = mdialog.findViewById(R.id.swipe_listing);
                close_popup = mdialog.findViewById(R.id.close_popup_btn);
                popup_listing_lay = mdialog.findViewById(R.id.textinput_price);
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
                                if (!TextUtils.isEmpty(popup_price.getText().toString())){
                                    if (Double.parseDouble(popup_price.getText().toString()) >0){
                                        swipe_listing.showResultIcon(true);

                                        nftAdd_price.setPrice(Double.parseDouble(popup_price.getText().toString()));

                                        String notif_listing = "Your NFT ("+ nft_title.getText().toString() + ") successfully listed for "+ popup_price.getText().toString() + " ETH";
                                        String history_note = nftAdd_price.owner + " listing NFT for "+ popup_price.getText().toString() + " ETH";
                                        String notif_listing_in = "NFT anda ("+ nft_title.getText().toString() + ") berhasil didaftarkan seharga "+ popup_price.getText().toString() + " ETH";
                                        String history_note_in = nftAdd_price.owner + " mendaftarkan nft seharga "+ popup_price.getText().toString() + " ETH";

                                        History history = new History(history_note, nftAdd_price.token);
                                        Notif notif_lister = new Notif(notif_listing, firebaseUser.getDisplayName(),"yes");
                                        History history_in = new History(history_note_in, nftAdd_price.token);
                                        Notif notif_lister_in = new Notif(notif_listing_in, firebaseUser.getDisplayName(),"yes");

                                        dataref4a.push().setValue(notif_lister);
                                        dataref2a.push().setValue(history);
                                        dataref4b.push().setValue(notif_lister_in);
                                        dataref2b.push().setValue(history_in);
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
                                    } else {
                                        swipe_listing.showResultIcon(false);
                                        popup_listing_lay.setError(getResources().getString(R.string.listing_failed));
                                    }
                                } else {
                                    swipe_listing.showResultIcon(false);
                                    popup_listing_lay.setError(getResources().getString(R.string.listing_failed2));
                                }

                            }
                        },2000);
                    }
                });
            }
        });
        nft_unlisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DetailNftActivity.this)
                        .setTitle(getResources().getString(R.string.unlis))
                        .setMessage(getResources().getString(R.string.unlis2))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                progressDialog = new ProgressDialog(DetailNftActivity.this);
                                progressDialog.setTitle(getResources().getString(R.string.loading));
                                progressDialog.setMessage(getResources().getString(R.string.wait));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        String notif_unl = "You has unlist NFT ("+ nft_title.getText().toString() +")";
                                        String notif_unl_in = "Anda telah membatalkan penjualan NFT ("+ nft_title.getText().toString() + ")";
                                        String history_note = nftAdd_price.owner + " has unlisted NFT";
                                        String history_note_in = nftAdd_price.owner + " telah membatalkan penjualan NFT";

                                        Notif notif_lister = new Notif(notif_unl, firebaseUser.getDisplayName(),"yes");
                                        Notif notif_lister_in = new Notif(notif_unl_in, firebaseUser.getDisplayName(),"yes");
                                        History history = new History(history_note, nftAdd_price.token);
                                        History history_in = new History(history_note_in, nftAdd_price.token);

                                        dataref4a.push().setValue(notif_lister);
                                        dataref4b.push().setValue(notif_lister_in);
                                        dataref2a.push().setValue(history);
                                        dataref2b.push().setValue(history_in);
                                        dataref.child(Nft_key).child("price").setValue(0);

                                        finish();
                                        startActivity(getIntent());
                                    }
                                }, 2000);

                            }})
                        .setNegativeButton(android.R.string.no, null).show();


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
                            popup_balance.setText(getResources().getString(R.string.welcome) + snapshot.child("balance").getValue().toString() + " ETH");
                            saldo = Double.parseDouble(snapshot.child("balance").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dataref3.child(nft_owner.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            saldo_penjual_nft = Double.parseDouble(snapshot.child("balance").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                popup_price.setText(getResources().getString(R.string.priceeth)+ nft_price.getText().toString());

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
                                    popup_balance_lay.setError(getResources().getString(R.string.notenoughbalance));

                                } else {
                                    Double saldo_akhir = saldo-harga;
                                    Double saldo_akhir_penjual = saldo_penjual_nft+harga;
                                    swipe_buy.showResultIcon(true);

                                    String history_note2 = nft_owner.getText().toString() + " transfer NFT to "+
                                            firebaseUser.getDisplayName() + " for "+ nft_price.getText().toString() + " ETH";
                                    String notif_pembeli = "You have buy NFT "+ nft_title.getText().toString()+ " from "+nft_owner.getText().toString();
                                    String notif_penjual = "Your NFT ("+ nft_title.getText().toString() + ") has been sold for "+ nft_price.getText().toString()
                                            +" ETH to "+ firebaseUser.getDisplayName();

                                    String history_note2_in = nft_owner.getText().toString() + " menjual NFT kepada "+
                                            firebaseUser.getDisplayName() + " seharga "+ nft_price.getText().toString() + " ETH";
                                    String notif_pembeli_in = "Anda telah membeli NFT "+ nft_title.getText().toString()+ " dari "+nft_owner.getText().toString();
                                    String notif_penjual_in = "NFT anda ("+ nft_title.getText().toString() + ") telah terjual seharga "+ nft_price.getText().toString()
                                            +" ETH kepada "+ firebaseUser.getDisplayName();

                                    History history = new History(history_note2, nftAdd_buy.token);
                                    Notif notif = new Notif(notif_pembeli, firebaseUser.getDisplayName(),"yes");
                                    Notif notif2 = new Notif(notif_penjual, nft_owner.getText().toString(),"yes");
                                    History history_in = new History(history_note2_in, nftAdd_buy.token);
                                    Notif notif_in = new Notif(notif_pembeli_in, firebaseUser.getDisplayName(),"yes");
                                    Notif notif2_in = new Notif(notif_penjual_in, nft_owner.getText().toString(),"yes");

                                    dataref2a.push().setValue(history);
                                    dataref4a.push().setValue(notif);
                                    dataref4a.push().setValue(notif2);
                                    dataref2b.push().setValue(history_in);
                                    dataref4b.push().setValue(notif_in);
                                    dataref4b.push().setValue(notif2_in);
                                    dataref3.child(firebaseUser.getDisplayName()).child("balance").setValue(saldo_akhir);
                                    dataref3.child(nft_owner.getText().toString()).child("balance").setValue(saldo_akhir_penjual);
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
        nft_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataref.child(Nft_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String img = snapshot.child("img").getValue().toString();
                            String title = snapshot.child("title").getValue().toString();
                            downloadImage(title, img);
                            new AlertDialog.Builder(DetailNftActivity.this)
                                    .setTitle(getResources().getString(R.string.download))
                                    .setMessage(getResources().getString(R.string.download2))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }}).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        nft_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DetailNftActivity.this)
                        .setTitle(getResources().getString(R.string.del))
                        .setMessage(getResources().getString(R.string.delcon))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                progressDialog = new ProgressDialog(DetailNftActivity.this);
                                progressDialog.setTitle(getResources().getString(R.string.loading));
                                progressDialog.setMessage(getResources().getString(R.string.wait));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        String notif_del = "You have deleted NFT ("+ nft_title.getText().toString() +")";
                                        String notif_del_in = "Anda telah menghapus NFT ("+ nft_title.getText().toString() + ")";

                                        Notif notif_lister = new Notif(notif_del, firebaseUser.getDisplayName(),"yes");
                                        Notif notif_lister_in = new Notif(notif_del_in, firebaseUser.getDisplayName(),"yes");

                                        dataref4a.push().setValue(notif_lister);
                                        dataref4b.push().setValue(notif_lister_in);
                                        dataref.child(Nft_key).removeValue();

                                        dataref2a.orderByChild("token").equalTo(nft_token.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String key = dataSnapshot.getKey();
                                                        dataref2a.child(key).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        dataref2b.orderByChild("token").equalTo(nft_token.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String key = dataSnapshot.getKey();
                                                        dataref2b.child(key).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        storageref.delete();

                                        finish();
                                    }
                                }, 2000);

                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        nft_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EditNFTActivity.class);
                intent.putExtra("Nft_key",Nft_key);
                intent.putExtra("Nft_token",nft_token.getText().toString());
                startActivity(intent);
            }
        });

    }

    private void downloadImage(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
        }catch (Exception e){
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadHistory(Query dataref2) {
        nft_options = new FirebaseRecyclerOptions.Builder<History>().setQuery(dataref2,History.class).build();
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault());
                holder.dateH.setText(dateFormat.format(model.dateBuy));
                holder.action.setText(model.getAksi());

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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