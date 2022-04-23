package com.dnx.trpam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SearchFragment extends Fragment {
    SearchView searchView;
    TextView textView;
    RecyclerView searchRecycler;
    FirebaseUser firebaseUser;
    DatabaseReference dataref;
    Query datarefQ;
    FirebaseRecyclerOptions<NftPost> nft_options;
    FirebaseRecyclerAdapter<NftPost,HomeViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        textView = view.findViewById(R.id.txtSearch);
        searchRecycler = view.findViewById(R.id.search_recycler);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                datarefQ = dataref.orderByChild("title").equalTo(query);
                searchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                searchRecycler.setHasFixedSize(true);
                LoadNFT(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    public void LoadNFT(String query) {
        nft_options = new FirebaseRecyclerOptions.Builder<NftPost>().setQuery(datarefQ,NftPost.class).build();
        adapter = new FirebaseRecyclerAdapter<NftPost, HomeViewHolder>(nft_options) {
            @Override
            public void onDataChanged() {
                if(getItemCount() == 0)
                    textView.setText("\"" + query + "\""+getResources().getString(R.string.notfound));
                    textView.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull NftPost model) {
                textView.setVisibility(View.INVISIBLE);
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
        searchRecycler.setAdapter(adapter);
    }

}
