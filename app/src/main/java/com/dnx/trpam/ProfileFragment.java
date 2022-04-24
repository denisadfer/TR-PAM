package com.dnx.trpam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Locale;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView txtName,txtEmail;
    Button btnLogout, btnEdit, btnChange, btnLang;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.profile_name);
        txtEmail = view.findViewById(R.id.profile_email);
        btnLogout = (Button) view.findViewById(R.id.logout_btn);
        btnEdit = (Button) view.findViewById(R.id.edit_btn);
        btnChange = (Button) view.findViewById(R.id.change_pass_btn);
        btnLang = (Button) view.findViewById(R.id.changelang);
        btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });

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

    private void showChangeLanguageDialog() {
        final String [] listItems = {"English (United States)","Indonesia"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle(getResources().getString(R.string.country));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en");
                    saveLocale("en");
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Change Language");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            getActivity().recreate();
                        }
                    }, 1000);

                }else if(i==1){
                    setLocale("in");
                    saveLocale("in");

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Change Language");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity(),MainActivity.class));
                        }
                    }, 1000);
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
    }

    private void saveLocale(String lang) {
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_btn:
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Sign out");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                }, 1000);


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