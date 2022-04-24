package com.dnx.trpam;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    TextView dateH,action;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        dateH = itemView.findViewById(R.id.history_nft_date);
        action = itemView.findViewById(R.id.history_nft_action);

    }
}
