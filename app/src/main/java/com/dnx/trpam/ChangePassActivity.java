package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        txtEmail = findViewById(R.id.txt_email);
        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);
        saveBtn = findViewById(R.id.save_btn);

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
    }

    public void changePass(String email, String oldPassword, String newPassword) {
        if(!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Password Changed", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    } else {
                        oldPass.setError("Password is incorrect");
                        oldPass.requestFocus();
                    }
                }
            });
        }
    }

}