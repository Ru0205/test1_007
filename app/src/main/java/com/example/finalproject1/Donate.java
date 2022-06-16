package com.example.finalproject1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject1.databinding.ActivityDonateBinding;
import com.example.finalproject1.databinding.ActivityHomeUserBinding;
import com.example.finalproject1.databinding.ActivityStationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Donate extends AppCompatActivity {

    private ActivityDonateBinding binding;

    private StorageReference storageRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_donate);


        binding = ActivityDonateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageRef = FirebaseStorage.getInstance().getReference();

        Intent it = getIntent();
        String staID = it.getStringExtra("Station");

        binding.txtSta.setText(staID + "站");

        Log.d("Demo", "staID: " + staID);
        Log.d("Demo", "uri: " + uri);


        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        binding.btnPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
                picker.setType("image/*");
                startActivityForResult(picker, 101);
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Demo", "按下確認紐");
                Log.d("Demo", "uri是否空:" + uri);
                Log.d("Demo", staID);

                String name = binding.editTextName.getText().toString();
                String snippet = binding.editTextSnippet.getText().toString();
                //String imgUri = uri.toString();

                if("".equals(name.trim()) ||uri == null)
                {
                    Toast.makeText(Donate.this,"請輸入名稱或者上傳圖片",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Map<String, Object> item = new HashMap<>();

                    item.put("name", name);
                    item.put("snippet", snippet);
                    item.put("located", staID);
                    item.put("ItemID",staID + uri.getLastPathSegment());

                    db.collection("Item")
                            .document(staID + uri.getLastPathSegment())
                            .set(item)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Donate.this, "新增成功", Toast.LENGTH_LONG).show();

                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Donate.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                    storageRef.child("Station" + staID).child(staID + uri.getLastPathSegment())
                            .putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Toast.makeText(Donate.this, "上傳成功:"+staID + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();

                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Donate.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data.getData();

            Log.d("Demo", "圖片uri: " + uri);

            Glide.with(Donate.this)
                    .load(uri)
                    .into(binding.imageView);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Demo","Donate onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Demo","Donate onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Demo","Donate onDestroy");
    }

}