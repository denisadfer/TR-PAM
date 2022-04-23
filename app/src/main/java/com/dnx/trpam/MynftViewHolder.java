package com.dnx.trpam;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MynftViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView title;

    public MynftViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.mynft_img);
        title = itemView.findViewById(R.id.mynft_title);
    }
}
