package com.dnx.trpam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MynftFragment extends Fragment{

    Button sell_btn;
    TextView mynft_nodata;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    DatabaseReference dataref;
    FirebaseRecyclerOptions<NftPost> nft_options;
    FirebaseRecyclerAdapter<NftPost,MynftViewHolder> adapter;
    Query datarefQ;
    boolean isHaveNft=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mynft, container, false);

        sell_btn = view.findViewById(R.id.sell_btn);
        recyclerView = view.findViewById(R.id.mynft_recycler);
        mynft_nodata = view.findViewById(R.id.mynft_nodata);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        datarefQ = dataref.orderByChild("owner").equalTo(firebaseUser.getDisplayName());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setHasFixedSize(true);
        LoadNFT();

        sell_btn.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(),AddMynft.class));
        });

        return view;
    }

    private void LoadNFT() {
        nft_options = new FirebaseRecyclerOptions.Builder<NftPost>().setQuery(datarefQ,NftPost.class).build();
        adapter = new FirebaseRecyclerAdapter<NftPost, MynftViewHolder>(nft_options) {
            // Add this
            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)
                    mynft_nodata.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onBindViewHolder(@NonNull MynftViewHolder holder, int position, @NonNull NftPost model) {
                holder.title.setText(model.getTitle());
                Picasso.get().load(model.getImg()).into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
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
            public MynftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_mynft,parent,false);
                return new MynftViewHolder(view);
            }
        };


        adapter.startListening();
        recyclerView.setAdapter(adapter);
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
