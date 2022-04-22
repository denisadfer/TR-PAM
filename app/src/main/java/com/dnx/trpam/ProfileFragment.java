package com.dnx.trpam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView txtName,txtEmail;
    Button btnLogout, btnEdit, btnChange;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.profile_name);
        txtEmail = view.findViewById(R.id.profile_email);
        btnLogout = (Button) view.findViewById(R.id.logout_btn);
        btnEdit = (Button) view.findViewById(R.id.edit_btn);
        btnChange = (Button) view.findViewById(R.id.change_pass_btn);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            txtName.setText(firebaseUser.getDisplayName());
            txtEmail.setText(firebaseUser.getEmail());
        } else {
            txtName.setText("Data Name is empty");
            txtEmail.setText("Data Email is empty");
        }

        btnLogout.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnChange.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_btn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.edit_btn:
                startActivity(new Intent(getActivity(), EditActivity.class));
                break;
            case R.id.change_pass_btn:
                startActivity(new Intent(getActivity(), ChangePassActivity.class));
                break;
        }
    }
}
