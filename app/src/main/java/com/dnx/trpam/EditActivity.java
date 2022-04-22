package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {
    EditText editName, editUser, editPass, editPass2, editEmail, editPhone;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editName = findViewById(R.id.fullnameedit);
        editEmail = findViewById(R.id.email);
        editUser = findViewById(R.id.username);
        editPhone = findViewById(R.id.phone);
        editPass = findViewById(R.id.pass1);
        editPass2 = findViewById(R.id.pass2);
        btnSave = findViewById(R.id.save_btn);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataUser");

        databaseReference.child(firebaseUser.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editName.setText(snapshot.child("name").getValue().toString());
                    editEmail.setText(snapshot.child("email").getValue().toString());
                    editUser.setText(snapshot.child("username").getValue().toString());
                    editPhone.setText(snapshot.child("phone").getValue().toString());
                    //editPass

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = editName.getText().toString();
                            String email = editEmail.getText().toString();
                            String userName = editUser.getText().toString();
                            String phone = editPhone.getText().toString();
                            //String pass =
                            //String pass2 =

                            editData(name, email, userName, phone);
                            Toast.makeText(getApplicationContext(),"Updated Successfully", Toast.LENGTH_LONG).show();
                            finish();
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

    public void editData(String name, String email, String userName, String phone){
        if(!TextUtils.isEmpty(name))
            FirebaseDatabase.getInstance().getReference("DataUser").child(userName).child("name").setValue(name);

        if(!TextUtils.isEmpty(email))
            FirebaseDatabase.getInstance().getReference("DataUser").child(userName).child("email").setValue(email);

        if(!TextUtils.isEmpty(userName))
            FirebaseDatabase.getInstance().getReference("DataUser").child(userName).child("username").setValue(userName);

        if(!TextUtils.isEmpty(phone))
            FirebaseDatabase.getInstance().getReference("DataUser").child(userName).child("phone").setValue(phone);

    }
}