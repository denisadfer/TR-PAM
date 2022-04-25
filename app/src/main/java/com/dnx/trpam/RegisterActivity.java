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
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText editName, editUser, editPass, editPass2, editEmail, editPhone;
    Button btnRegis;
    TextView loginHere, language;
    private ProgressDialog progressDialog, progressDialog2;
    private FirebaseAuth mAuth;
    boolean isExist=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.fullname);
        editEmail = findViewById(R.id.email);
        editUser = findViewById(R.id.username);
        editPhone = findViewById(R.id.phone);
        editPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        editPass = findViewById(R.id.pass1);
        editPass2 = findViewById(R.id.pass2);
        btnRegis = findViewById(R.id.regis_btn1);
        loginHere = findViewById(R.id.loginHere);
        language = findViewById(R.id.language);

        language.setOnClickListener(view -> showChangeLanguageDialog());

        mAuth = FirebaseAuth.getInstance();
        progressDialog2 = new ProgressDialog(RegisterActivity.this);
        progressDialog2.setTitle(getResources().getString(R.string.loading));
        progressDialog2.setMessage(getResources().getString(R.string.waiting));
        progressDialog2.setCancelable(false);

        loginHere.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),LoginActivity.class)));

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();


        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference();


        mFirebaseInstance.getReference("app_title").setValue("TR PAM");


        btnRegis.setOnClickListener(view -> {
            String vName = editName.getText().toString();
            String vEmail = editEmail.getText().toString();
            String vUser = editUser.getText().toString();
            String vPhone = editPhone.getText().toString();
            String vPass = editPass.getText().toString();
            String vPass2 = editPass2.getText().toString();

            if (vName.isEmpty()) {
                editName.setError(getResources().getString(R.string.fillform));
                editName.requestFocus();
                return;
            }
            if (vEmail.isEmpty()) {
                editEmail.setError(getResources().getString(R.string.fillform));
                editEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(vEmail).matches()) {
                editEmail.setError(getResources().getString(R.string.emailcorrect));
                editEmail.requestFocus();
                return;
            }
            if (vUser.isEmpty()) {
                editUser.setError(getResources().getString(R.string.fillform));
                editUser.requestFocus();
                return;
            }
            if (vPhone.isEmpty()) {
                editPhone.setError(getResources().getString(R.string.fillform));
                editPhone.requestFocus();
                return;
            }
            if (vPass.isEmpty()) {
                editPass.setError(getResources().getString(R.string.fillform));
                editPass.requestFocus();
                return;
            }
            if (vPass2.isEmpty()) {
                editPass2.setError(getResources().getString(R.string.fillform));
                editPass2.requestFocus();
                return;
            }

            if (vPass.equals(vPass2)){

                mFirebaseDatabase.child("DataUser").orderByChild("username").equalTo(vUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isExist=false;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);

                            if (user.getUsername().equals(vUser)){
                                isExist=true;
                            }
                        }

                        if (!isExist) {
                            progressDialog2.show();
                            mAuth.createUserWithEmailAndPassword(vEmail, vPass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                FirebaseUser user = task.getResult().getUser();
                                                if (user!=null){
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(vUser).build();

                                                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            int balance = 0;


                                                            User inputUser = new User(vName, vEmail, vPhone, vUser, (double) balance);

                                                            mFirebaseDatabase.child("DataUser").child(vUser).setValue(inputUser);
                                                            reload();
                                                            finish();

                                                        }
                                                    });
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "Register failed, server error",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog2.dismiss();
                                        }
                                    });
                        } else {
                            editUser.setError(getResources().getString(R.string.alreadyexist));
                            editUser.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }else {
                editPass.setError(getResources().getString(R.string.passwordmatch));
                editPass.requestFocus();
            }

        });
    }

    private void showChangeLanguageDialog(){
        final String [] listItems = {"English (United States)","Indonesia"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.country));
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if(i==0){
                setLocale("en");
                progressDialog = new ProgressDialog(RegisterActivity.this);
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
                progressDialog = new ProgressDialog(RegisterActivity.this);
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
        finish();

    }
}