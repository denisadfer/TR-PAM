package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    EditText editUser, editPass;
    Button btnLogin;
    TextView registerNow, language;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);

        editUser = findViewById(R.id.user);
        editPass = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.login_btn);
        registerNow = findViewById(R.id.registerNow);
        language = findViewById(R.id.language);

        language.setOnClickListener(view -> showChangeLanguageDialog());

        mAuth = FirebaseAuth.getInstance();

        registerNow.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),RegisterActivity.class)));

        btnLogin.setOnClickListener(view -> {
            if (!Patterns.EMAIL_ADDRESS.matcher(editUser.getText().toString()).matches()) {
                editUser.setError(getResources().getString(R.string.emailcorrect));
                editUser.requestFocus();
                return;
            } if (TextUtils.isEmpty(editPass.getText().toString())) {
                editUser.setError(getResources().getString(R.string.fillform));
                editUser.requestFocus();
                return;
            }
            if (editUser.getText().length()>0 && editPass.getText().length()>0){
                validlogin(editUser.getText().toString(), editPass.getText().toString());
            }
        });
    }

    private void showChangeLanguageDialog(){
        final String [] listItems = {"English (United States)","Indonesia"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.country));
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if(i==0){
                setLocale("en");
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle(getResources().getString(R.string.change));
                progressDialog.setMessage(getResources().getString(R.string.wait));
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressDialog.dismiss();
                    recreate();
                }, 1000);
            }else if(i==1){
                setLocale("in");
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle(getResources().getString(R.string.change));
                progressDialog.setMessage(getResources().getString(R.string.wait));
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressDialog.dismiss();
                    recreate();
                }, 1000);

            }
            dialogInterface.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }

    private void validlogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult()!=null){
                    if (task.getResult().getUser()!=null){
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setTitle(getResources().getString(R.string.sigin));
                        progressDialog.setMessage(getResources().getString(R.string.wait));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            progressDialog.dismiss();
                            reload();
                        }, 1000);
                    }else {
                        Toast.makeText(getApplicationContext(), "Login failed, server error",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        editUser.setError(getResources().getString(R.string.emailregistered));
                        editUser.requestFocus();
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        editPass.setError(getResources().getString(R.string.passincorrect));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}