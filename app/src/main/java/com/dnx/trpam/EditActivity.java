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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {
    EditText editName, editPass, editPhone;
    Button btnSave;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editName = findViewById(R.id.fullname);
        editPhone = findViewById(R.id.phone);
        editPass = findViewById(R.id.pass1);
        btnSave = findViewById(R.id.save_btn);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataUser");

        databaseReference.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editName.setText(snapshot.child("name").getValue().toString());
                    editPhone.setText(snapshot.child("phone").getValue().toString());

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = editName.getText().toString();
                            String userName = snapshot.child("username").getValue().toString();
                            String phone = editPhone.getText().toString();
                            String pass = editPass.getText().toString();

                            editData(name, userName, phone, pass);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void editData(String name, String userName, String phone, String pass){
        if(!TextUtils.isEmpty(pass)) {
            String email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (!TextUtils.isEmpty(name))
                            FirebaseDatabase.getInstance().getReference("DataUser").child(userName).child("name").setValue(name);

                        if (!TextUtils.isEmpty(phone))
                            FirebaseDatabase.getInstance().getReference("DataUser").child(userName).child("phone").setValue(phone);
                        progressDialog = new ProgressDialog(EditActivity.this);
                        progressDialog.setTitle("Loading");
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(EditActivity.this)
                                        .setTitle("Profile changed")
                                        .setMessage("Your profile has been changed successfully")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                finish();
                                            }}).show();
                            }
                        }, 1000);

                    } else {
                        editPass.setError("Password is incorrect");
                        editPass.requestFocus();
                    }
                }
            });
        }else {
            editPass.setError("Enter your password");
            editPass.requestFocus();
        }
    }
}