package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {
    TextView txtEmail;
    EditText oldPass, newPass;
    Button saveBtn;
    ImageView backc;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        txtEmail = findViewById(R.id.txt_email);
        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);
        saveBtn = findViewById(R.id.save_btn);
        backc = findViewById(R.id.back_cange);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = firebaseUser.getEmail();
        txtEmail.setText(email);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String oldPassword = oldPass.getText().toString();
                String newPassword = newPass.getText().toString();
                changePass(email,oldPassword,newPassword);
            }
        });
        backc.setOnClickListener(view -> {
            finish();
        });

    }

    public void changePass(String email, String oldPassword, String newPassword) {
        if(!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)) {
            if(oldPassword.equals(newPassword)){
                new AlertDialog.Builder(ChangePassActivity.this)
                        .setTitle(getResources().getString(R.string.passchangeunsuc))
                        .setMessage(getResources().getString(R.string.newpassunsuc))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }}).show();
            } else {
                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog = new ProgressDialog(ChangePassActivity.this);
                                    progressDialog.setTitle(getResources().getString(R.string.loading));
                                    progressDialog.setMessage(getResources().getString(R.string.wait));
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        progressDialog.dismiss();
                                        new AlertDialog.Builder(ChangePassActivity.this)
                                                .setTitle(getResources().getString(R.string.passchange))
                                                .setMessage(getResources().getString(R.string.passchangesuc))
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        FirebaseAuth.getInstance().signOut();
                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                    }}).show();
                                    }, 1000);

                                }
                            });
                        } else {
                            oldPass.setError(getResources().getString(R.string.passincorrect));
                            oldPass.requestFocus();
                        }
                    }
                });
            }

        } else if (TextUtils.isEmpty(oldPassword)){
            oldPass.setError(getResources().getString(R.string.enteroldpass));
            oldPass.requestFocus();
        } else if (TextUtils.isEmpty(newPassword)){
            newPass.setError(getResources().getString(R.string.enternewpass));
            newPass.requestFocus();
        }
    }

}