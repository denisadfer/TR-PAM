package com.dnx.trpam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MynftFragment extends Fragment{

    Button sell_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mynft, container, false);

        sell_btn = view.findViewById(R.id.sell_btn);

        sell_btn.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(),AddMynft.class));
        });

        return view;
    }
}
