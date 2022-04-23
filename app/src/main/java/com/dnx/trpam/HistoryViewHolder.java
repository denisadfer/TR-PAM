package com.dnx.trpam;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    TextView dateH,owner,action,newOwner, price;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        dateH = itemView.findViewById(R.id.history_nft_date);
        owner = itemView.findViewById(R.id.history_nft_owner);
        action = itemView.findViewById(R.id.history_nft_action);
        newOwner = itemView.findViewById(R.id.history_nft_neworner);
        price = itemView.findViewById(R.id.history_nft_price);
    }
}
