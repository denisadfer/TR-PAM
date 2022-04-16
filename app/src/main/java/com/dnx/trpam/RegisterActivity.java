package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    EditText editName, editUser, editPass, editPass2, editEmail, editPhone;
    Button btnRegis;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editName = findViewById(R.id.fullname);
        editEmail = findViewById(R.id.email);
        editUser = findViewById(R.id.username);
        editPhone = findViewById(R.id.phone);
        editPass = findViewById(R.id.pass1);
        editPass2 = findViewById(R.id.pass2);
        btnRegis = findViewById(R.id.regis_btn1);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Waiting for Log in");
        progressDialog.setCancelable(false);

        btnRegis.setOnClickListener(view -> {
            if (editName.getText().length()>0 && editUser.getText().length()>0 && editEmail.getText().length()>0
                    && editPhone.getText().length()>0 && editPass.getText().length()>0 && editPass2.getText().length()>0){
                if (editPass.getText().toString().equals(editPass2.getText().toString())){
                    validregister(editName.getText().toString(), editUser.getText().toString(),
                            editPhone.getText().toString(), editEmail.getText().toString(), editPass.getText().toString());
                }else {
                    Toast.makeText(this, "Please, input the same password", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Please, fill all forms", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validregister(String name, String user, String phone, String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult().getUser()!=null){
                            FirebaseUser user = task.getResult().getUser();
                            if (user!=null){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        reload();
                                    }
                                });

                            }else {
                                Toast.makeText(getApplicationContext(), "Register Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void reload(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
}