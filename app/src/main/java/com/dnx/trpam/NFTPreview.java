package com.dnx.trpam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class NFTPreview extends AppCompatActivity {

    ImageView nftPreview;
    String img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nft_preview);
        Intent intent = getIntent();
        img = intent.getStringExtra("img");

        nftPreview = findViewById(R.id.nftPreview);
        Glide.with(NFTPreview.this).load(img).into(nftPreview);
    }
}
