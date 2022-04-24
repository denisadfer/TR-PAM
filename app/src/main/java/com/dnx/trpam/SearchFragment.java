package com.dnx.trpam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SearchFragment extends Fragment {
    SearchView searchView;
    FloatingActionButton sortBtn;
    TextView textView;
    RecyclerView searchRecycler;
    FirebaseUser firebaseUser;
    DatabaseReference dataref;
    Query datarefQ;
    private ProgressDialog progressDialog;
    FirebaseRecyclerOptions<NftPost> nft_options;
    FirebaseRecyclerAdapter<NftPost,HomeViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        textView = view.findViewById(R.id.txtSearch);
        searchRecycler = view.findViewById(R.id.search_recycler);
        sortBtn = view.findViewById(R.id.sort_btn);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortDialog();
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textView.setText("\"" + query + "\""+getResources().getString(R.string.notfound));
                datarefQ = dataref.orderByChild("title").startAt(query).endAt(query + "\uf8ff");;
                searchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                searchRecycler.setHasFixedSize(true);
                LoadNFT();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
        return view;
    }

    private void showSortDialog() {
        final String [] listItems = {
                getResources().getString(R.string.title),
                getResources().getString(R.string.owner),
                getResources().getString(R.string.price)
        };
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle(getResources().getString(R.string.sort));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i) {
                    case 0:
                        datarefQ = dataref.orderByChild("title");
                        break;
                    case 1:
                        datarefQ = dataref.orderByChild("owner");
                        break;
                    case 2:
                        datarefQ = dataref.orderByChild("price");
                        break;
                }
                searchRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                searchRecycler.setHasFixedSize(true);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        LoadNFT();
                        dialogInterface.dismiss();
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                    }
                }, 1000);

            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void LoadNFT() {
        nft_options = new FirebaseRecyclerOptions.Builder<NftPost>().setQuery(datarefQ,NftPost.class).build();
        adapter = new FirebaseRecyclerAdapter<NftPost, HomeViewHolder>(nft_options) {
            @Override
            public void onDataChanged() {
                if(getItemCount() == 0)
                    textView.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull NftPost model) {
                textView.setVisibility(View.INVISIBLE);
                if (model.getOwner().equals(firebaseUser.getDisplayName())){
                    holder.buy.setText("Detail");
                } if (model.getPrice() > 0){
                    holder.price.setText(String.valueOf(model.getPrice()));
                } else {
                    holder.price.setText(getResources().getString(R.string.notlisted));
                    holder.buy.setText("Details");
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
        searchRecycler.setAdapter(adapter);
    }

}