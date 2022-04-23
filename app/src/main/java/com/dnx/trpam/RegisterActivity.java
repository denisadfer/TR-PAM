package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button btnRegis, btnLang;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
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
        editPass = findViewById(R.id.pass1);
        editPass2 = findViewById(R.id.pass2);
        btnRegis = findViewById(R.id.regis_btn1);
        btnLang = findViewById(R.id.changelang);
        btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Waiting for Login");
        progressDialog.setCancelable(false);

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
                editName.setError("Please, fill the form");
                editName.requestFocus();
                return;
            }
            if (vEmail.isEmpty()) {
                editEmail.setError("Please, fill the form");
                editEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(vEmail).matches()) {
                editEmail.setError("Your email address doesn't correct");
                editEmail.requestFocus();
                return;
            }
            if (vUser.isEmpty()) {
                editUser.setError("Please, fill the form");
                editUser.requestFocus();
                return;
            }
            if (vPhone.isEmpty()) {
                editPhone.setError("Please, fill the form");
                editPhone.requestFocus();
                return;
            }
            if (vPass.isEmpty()) {
                editPass.setError("Please, fill the form");
                editPass.requestFocus();
                return;
            }
            if (vPass2.isEmpty()) {
                editPass2.setError("Please, fill the form");
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
                            progressDialog.show();
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
                                                            User inputUser = new User(vName, vEmail, vPhone, vUser);

                                                            mFirebaseDatabase.child("DataUser").child(vUser).setValue(inputUser);
                                                            reload();
                                                            finish();

                                                        }
                                                    });
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "Register Error",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        } else {
                            editUser.setError("Username's already exist!");
                            editUser.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }else {
                Toast.makeText(this, "Your password doesn't matched", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void showChangeLanguageDialog(){
        final String [] listItems = {"English (United States)","Indonesia"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.country));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en");
                    recreate();
                }else if(i==1){
                    setLocale("in");
                    recreate();
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
}