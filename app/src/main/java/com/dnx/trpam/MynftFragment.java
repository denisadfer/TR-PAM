package com.dnx.trpam;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    ImageView notif_isnotnew;
    TextView mynft_nodata;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    DatabaseReference dataref, dataref2a, dataref2b;
    FirebaseRecyclerOptions<NftPost> nft_options;
    FirebaseRecyclerAdapter<NftPost,MynftViewHolder> adapter;
    Query datarefQ;
    boolean isHaveNft=false;
    private boolean autorefresh = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mynft, container, false);

        sell_btn = view.findViewById(R.id.sell_btn);
        recyclerView = view.findViewById(R.id.mynft_recycler);
        mynft_nodata = view.findViewById(R.id.mynft_nodata);
        notif_isnotnew = view.findViewById(R.id.notif_isnotnew);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        dataref2a = FirebaseDatabase.getInstance().getReference().child("Notif_ENG");
        dataref2b = FirebaseDatabase.getInstance().getReference().child("Notif_IN");
        datarefQ = dataref.orderByChild("owner").equalTo(firebaseUser.getDisplayName());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setHasFixedSize(true);
        LoadNFT();

        //cek satu reference aja karna in dan eng dibuat bersamaan
        dataref2a.orderByChild("userNotif").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isNotifRead=true;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Notif notif = dataSnapshot.getValue(Notif.class);

                    if (notif.getIsNew().equals("yes")){
                        isNotifRead=false;
                    }
                }
                if (isNotifRead){
                    notif_isnotnew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(),NotifActivity.class));
                        }
                    });
                } else {
                    notif_isnotnew.setImageResource(R.drawable.ic_baseline_notification_important_24);
                    notif_isnotnew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //disini baru dibaca 22nya
                            dataref2a.orderByChild("userNotif").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Notif notif = dataSnapshot.getValue(Notif.class);

                                            if (notif.getIsNew().equals("yes")){
                                                String key = dataSnapshot.getKey();
                                                dataref2a.child(key).child("isNew").setValue("no");
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            dataref2b.orderByChild("userNotif").equalTo(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            Notif notif = dataSnapshot.getValue(Notif.class);

                                            if (notif.getIsNew().equals("yes")){
                                                String key = dataSnapshot.getKey();
                                                dataref2b.child(key).child("isNew").setValue("no");
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            startActivity(new Intent(getActivity(),NotifActivity.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(autorefresh){
            // refresh fragment
            Fragment fragment = new MynftFragment();
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
