package com.dnx.trpam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class AddMynft extends AppCompatActivity {

    Button btn_nft;
    TextView token_nft;
    EditText title_nft, desc_nft;
    ImageView img_nft, backnft;
    String token_img;
    DatabaseReference dataref, dataref2a, dataref2b, dataref3a, dataref3b;
    StorageReference storageref;
    Uri imageUri;


    boolean IsImageAdded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mynft);

        title_nft = findViewById(R.id.create_title_nft);
        desc_nft = findViewById(R.id.description_nft);
        token_nft = findViewById(R.id.create_token_nft);
        btn_nft = findViewById(R.id.create_btn_nft);
        img_nft = findViewById(R.id.create_img_nft);
        backnft = findViewById(R.id.back_addnft);

        dataref = FirebaseDatabase.getInstance().getReference().child("Nft_Post");
        dataref2a = FirebaseDatabase.getInstance().getReference().child("History_ENG");
        dataref3a = FirebaseDatabase.getInstance().getReference().child("Notif_ENG");
        dataref2b = FirebaseDatabase.getInstance().getReference().child("History_IN");
        dataref3b = FirebaseDatabase.getInstance().getReference().child("Notif_IN");
        storageref = FirebaseStorage.getInstance().getReference().child("Nft_images");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        img_nft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddMynft.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                    //kalau tidak di allow permission
                    ActivityCompat.requestPermissions(AddMynft.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    , 100);
                } else {
                    //kalau di allow
                    select_image();
                }
            }
        });

        backnft.setOnClickListener(view -> {
            finish();
        });

        btn_nft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String owner = firebaseUser.getDisplayName();
                String creator = owner;
                String title = title_nft.getText().toString();
                String desc = desc_nft.getText().toString();
                String token = token_nft.getText().toString();
                int price = 0;

                if (!IsImageAdded) {
                    new AlertDialog.Builder(AddMynft.this)
                            .setTitle(getResources().getString(R.string.addnft_dialog_mis))
                            .setMessage(getResources().getString(R.string.addnft_dialog_mis2))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }}).show();
                    return;
                }
                if (title.isEmpty()){
                    title_nft.setError(getResources().getString(R.string.addnft_name));
                    title_nft.requestFocus();
                    return;
                }
                if (desc.isEmpty()){
                    desc_nft.setError(getResources().getString(R.string.addnft_about));
                    desc_nft.requestFocus();
                    return;
                }
                if (token.isEmpty()){
                    new AlertDialog.Builder(AddMynft.this)
                            .setTitle(getResources().getString(R.string.addnft_dialog_mis))
                            .setMessage(getResources().getString(R.string.addnft_dialog_mis))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }}).show();
                    return;
                }
                if (IsImageAdded==true && title!=null && token!=null){
                    dataref.orderByChild("token").equalTo(token);
                    dataref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean IstokenAdded=false;
                            for (DataSnapshot nft : snapshot.getChildren()){
                                NftPost nftPost = nft.getValue(NftPost.class);

                                if (nftPost.getToken().equals(token)){
                                    IstokenAdded=true;
                                }
                            }
                            if (!IstokenAdded){
                                uploadImage(owner,creator,title,desc,token,price);
                            } else {
                                new AlertDialog.Builder(AddMynft.this)
                                        .setTitle(getResources().getString(R.string.addnft_dialog_failed))
                                        .setMessage(getResources().getString(R.string.addnft_dialog_failed2))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                            }}).show();
                            }
//


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }
        });

    }

    private void uploadImage(String owner, String creator, String title, String desc, String token, int price) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle(getResources().getString(R.string.addnft_dialog));
        pd.show();

        String key = dataref.push().getKey();
        storageref.child(key+".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageref.child(key +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("img",uri.toString());
                        hashMap.put("owner",owner);
                        hashMap.put("price",price);
                        hashMap.put("creator",creator);
                        hashMap.put("title",title);
                        hashMap.put("token",token);
                        hashMap.put("description",desc);
                        String notif_creator = "You Have Created NFT "+ title;
                        String histori_note = owner +" has created NFT ";
                        History history = new History(histori_note,token);
                        Notif notif = new Notif(notif_creator, owner,"yes");

                        String notif_creator_in = "Anda telah membuat NFT dengan nama "+ title;
                        String histori_note_in = owner +" membuat NFT ";
                        History history_in = new History(histori_note_in,token);
                        Notif notif_in = new Notif(notif_creator_in, owner,"yes");

                        dataref3a.push().setValue(notif);
                        dataref2a.push().setValue(history);
                        dataref3b.push().setValue(notif_in);
                        dataref2b.push().setValue(history_in);
                        dataref.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Database error, Failed to Create Nft", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progresPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage(getResources().getString(R.string.addnft_dialog2)+ (int) progresPercent + "%");
            }
        });
    }


    private void select_image (){
        title_nft.setText("");
        token_nft.setText("");
        img_nft.setImageResource(R.drawable.ic_baseline_image_24);

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        startActivityForResult(intent, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            select_image();
        } else {
            new AlertDialog.Builder(AddMynft.this)
                    .setTitle(getResources().getString(R.string.addnft_permiss))
                    .setMessage(getResources().getString(R.string.addnft_permiss2))
                    .setPositiveButton(getResources().getString(R.string.oset1), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }}).setNegativeButton(getResources().getString(R.string.oset2), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            IsImageAdded = true;
//            Glide.with(this).load(imageUri).into(img_nft);
            img_nft.setImageURI(imageUri);

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

                byte [] bytes = stream.toByteArray();

                token_img = Base64.encodeToString(bytes,Base64.DEFAULT);


            } catch (IOException e) {
                e.printStackTrace();
            }
            token_nft.setText(mhash(token_img));
            token_nft.setVisibility(View.VISIBLE);

        }
    }

    private String mhash(String token_img) {
        try {

            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(token_img.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexNumber = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexNumber.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexNumber.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }


}