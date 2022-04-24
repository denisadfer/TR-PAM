package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotifActivity extends AppCompatActivity {

    RecyclerView notif_recycler;
    TextView notif_nodoata;
    FirebaseUser firebaseUser;
    DatabaseReference dataref;
    FirebaseRecyclerOptions<Notif> nft_options;
    FirebaseRecyclerAdapter<Notif,NotifViewHolder> adapter;
    Query datarefq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        notif_recycler = findViewById(R.id.notif_recycler);
        notif_nodoata = findViewById(R.id.notif_nodata);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dataref = FirebaseDatabase.getInstance().getReference().child("Notif");

        datarefq = dataref.orderByChild("userNotif").equalTo(firebaseUser.getDisplayName());

        notif_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        notif_recycler.setHasFixedSize(true);
        LoadNotif();


    }

    private void LoadNotif() {
        nft_options = new FirebaseRecyclerOptions.Builder<Notif>().setQuery(datarefq,Notif.class).build();
        adapter = new FirebaseRecyclerAdapter<Notif, NotifViewHolder>(nft_options) {
            // Add this
            @Override
            public void onDataChanged() {
                // do your thing
                if(getItemCount() == 0)
                    notif_nodoata.setVisibility(View.VISIBLE);
                }

            @Override
            protected void onBindViewHolder(@NonNull NotifViewHolder holder, int position, @NonNull Notif model) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault());
                holder.dateDapet.setText(dateFormat.format(model.dateNotif));
                holder.isiNotif.setText(model.getNotif());

            }

            @NonNull
            @Override
            public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notif,parent,false);
                return new NotifViewHolder(view);
            }
        };


        adapter.startListening();
        notif_recycler.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));

    }

}