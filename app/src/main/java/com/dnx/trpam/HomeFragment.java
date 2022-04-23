package com.dnx.trpam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    TextView TxtName, balance;
    ImageView cardImg;
    RecyclerView homeRecycler;
    private FirebaseUser firebaseUser;
    FirebaseRecyclerOptions<NftPost> nft_options;
    FirebaseRecyclerAdapter<NftPost,HomeViewHolder>adapter;
    DatabaseReference dataref, dataref2;
    Query datarefQ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TxtName = view.findViewById(R.id.textName);
        balance = view.findViewById(R.id.home_balance);
        homeRecycler = view.findViewById(R.id.home_recycler);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        dataref2 = FirebaseDatabase.getInstance().getReference().child("DataUser");

        dataref2.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    balance.setText(snapshot.child("balance").getValue().toString() + " ETH");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // yang baru di post nftnya atau belum dijual maka ga akan ditampilkan di list home
        datarefQ = dataref.orderByChild("price").startAfter(0);

        if (firebaseUser!= null) {
            TxtName.setText(getResources().getString(R.string.welcome)+" "+firebaseUser.getDisplayName());
        } else {
            TxtName.setText("Data Name is empty");
        }

        homeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeRecycler.setHasFixedSize(true);
        LoadNFT();

        return view;
    }

    private void LoadNFT() {
        nft_options = new FirebaseRecyclerOptions.Builder<NftPost>().setQuery(datarefQ,NftPost.class).build();
        adapter = new FirebaseRecyclerAdapter<NftPost, HomeViewHolder>(nft_options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull NftPost model) {

                if (model.getOwner().equals(firebaseUser.getDisplayName())){
                    holder.buy.setText("Detail");
                }
                holder.price.setText(String.valueOf(model.getPrice()));
                holder.title.setText(model.getTitle());
                holder.owner.setText(model.getOwner());


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