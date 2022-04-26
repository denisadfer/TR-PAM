package com.dnx.trpam;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment{
    TextView TxtName, balance, balance_sensor;
    ImageView visi_saldo;
    Button topup;
    RecyclerView homeRecycler;
    FloatingActionButton scrollup;
    private FirebaseUser firebaseUser;
    FirebaseRecyclerOptions<NftPost> nft_options;
    FirebaseRecyclerAdapter<NftPost,HomeViewHolder>adapter;
    DatabaseReference dataref, dataref2;
    Query datarefQ;
    private boolean autorefresh = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TxtName = view.findViewById(R.id.textName);
        balance = view.findViewById(R.id.home_balance);
        balance_sensor = view.findViewById(R.id.home_balance_sensor);
        homeRecycler = view.findViewById(R.id.home_recycler);
        topup = view.findViewById(R.id.topup);
        scrollup = view.findViewById(R.id.scrollup_home);
        visi_saldo = view.findViewById(R.id.visi_saldo);

        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),TopUp.class));
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        dataref2 = FirebaseDatabase.getInstance().getReference().child("DataUser");


        // yang baru di post nftnya atau belum dijual maka ga akan ditampilkan di list home
        datarefQ = dataref.orderByChild("price").startAfter(0);

        homeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
//        homeRecycler.setHasFixedSize(true);
        LoadNFT();

        homeRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) { // scrolling down
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollup.setVisibility(View.VISIBLE);
                        }
                    }, 100); // delay hiding

                } else if (dy < 0) { // scrolling up

                    scrollup.setVisibility(View.GONE);
                }
            }

        });

        dataref2.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    balance.setText(snapshot.child("balance").getValue().toString().trim() + " ETH");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        scrollup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//          homeRecycler.scrollToPosition(0);
                homeRecycler.smoothScrollToPosition(0);

            }
        });

        visi_saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (balance.getVisibility() == View.VISIBLE){
                    balance.setVisibility(View.GONE);
                    balance_sensor.setVisibility(View.VISIBLE);
                    visi_saldo.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                } else {
                    balance.setVisibility(View.VISIBLE);
                    balance_sensor.setVisibility(View.GONE);
                    visi_saldo.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
            }
        });

        return view;
    }

    private void LoadNFT() {
        nft_options = new FirebaseRecyclerOptions.Builder<NftPost>().setQuery(dataref,NftPost.class).build();
        adapter = new FirebaseRecyclerAdapter<NftPost, HomeViewHolder>(nft_options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull NftPost model) {

                if (model.getOwner().equals(firebaseUser.getDisplayName())){
                    holder.buy.setText(getResources().getString(R.string.details));
                }
                if (model.getPrice() > 0){
                    holder.price.setText(String.valueOf(model.getPrice()));
                } else {
                    holder.price.setText(getResources().getString(R.string.notlisted));
                    holder.buy.setText(getResources().getString(R.string.details));
                }

                holder.title.setText(model.getTitle());


                Picasso.get().load(model.getImg()).into(holder.imageView);
                holder.buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),DetailNftActivity.class);
                        intent.putExtra("Nft_Post_key",getRef(holder.getAdapterPosition()).getKey());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_home,parent,false);
                return new HomeViewHolder(view);
            }
        };
        adapter.startListening();
        homeRecycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(autorefresh){
            // refresh fragment
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_container,fragment).commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        autorefresh = true;
    }

//    @Override
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