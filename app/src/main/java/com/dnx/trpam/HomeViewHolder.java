package com.dnx.trpam;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView title, owner, price;

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.home_img);
        title = itemView.findViewById(R.id.nft_name_home);
        owner = itemView.findViewById(R.id.nft_owner_home);
        price = itemView.findViewById(R.id.nft_price_home);
    }
}
