package com.dnx.trpam;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotifViewHolder extends RecyclerView.ViewHolder {

    TextView dateDapet, isiNotif;
    public NotifViewHolder(@NonNull View itemView) {
        super(itemView);
        dateDapet = itemView.findViewById(R.id.notif_tgl);
        isiNotif = itemView.findViewById(R.id.notif_notifnya);
    }
}
