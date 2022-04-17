package com.dnx.trpam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment{
    TextView TxtName;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TxtName = view.findViewById(R.id.textName);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!= null) {
            TxtName.setText("Welcome, "+firebaseUser.getDisplayName());
        } else {
            TxtName.setText("Data Name is empty");
        }

        return view;
    }
}