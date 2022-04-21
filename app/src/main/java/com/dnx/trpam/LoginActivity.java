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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText editUser, editPass;
    Button btnLogin, btnRegis;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUser = findViewById(R.id.user);
        editPass = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.login_btn);
        btnRegis = findViewById(R.id.regis_btn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Waiting for Log in");
        progressDialog.setCancelable(false);

        btnRegis.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));

        });

        btnLogin.setOnClickListener(view -> {
            if (editUser.getText().length()>0 && editPass.getText().length()>0){
                validlogin(editUser.getText().toString(), editPass.getText().toString());
            }else {
                Toast.makeText(this, "Please, fill all forms", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validlogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult()!=null){
                    if (task.getResult().getUser()!=null){
                        reload();
                    }else {
                        Toast.makeText(getApplicationContext(), "Login failed",
                                Toast.LENGTH_SHORT).show();

                    }

                }else{
                    if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        editUser.setError("Email not registered");
                        editUser.requestFocus();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        editPass.setError("Password is incorrect");
                        editPass.requestFocus();
                    }
                }

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