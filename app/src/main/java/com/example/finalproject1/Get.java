package com.example.finalproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject1.databinding.ActivityGetBinding;
import com.example.finalproject1.databinding.ActivityHomeUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Get extends AppCompatActivity {

    private ActivityGetBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference =
            FirebaseStorage.getInstance().getReference();

    List<String> items = new ArrayList<>();
    List<String> itemID = new ArrayList<>();
    String staID;
    String status;
    String itemPictPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_get);

        binding = ActivityGetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent it = getIntent();
        staID = it.getStringExtra("Station");
        status = it.getStringExtra("Status");

        Log.d("Demo", "?????????Status: " + status);

        binding.txtSta.setText("??????????????? (" + staID + "???)");

        //?????????????????????"??????"????????????
        if (status.equals("manager")) {
            Log.d("Demo", "manager??????:??????");
            binding.btnConfirm.setText("??????");
        } else if (status.equals("user")) {
            binding.btnConfirm.setText("??????");
        }

        loadItem();

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Demo", "?????????id: " + itemID.get(position));

                binding.txtSel.setText(items.get(position));
                itemPictPos = itemID.get(position);

                Log.d("Demo", "??????????????? ?????????: " + items.get(position));

                storageReference.child("/Station" + staID).child(itemID.get(position))
                        .getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Glide.with(Get.this)
                                        .load(uri)
                                        .into(binding.imageView);

                                Log.d("Demo", itemID.get(position) + "?????????" + uri);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Get.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Get.this.finish();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(binding.txtSel.getText())) {
                    Toast.makeText(Get.this, "???????????????", Toast.LENGTH_LONG).show();
                } else {

                    AlertDialog.Builder ad = new AlertDialog.Builder(Get.this);
                    ad.setTitle("??????");
                    ad.setMessage("?????????????????????????");
                    ad.setPositiveButton("???", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {

                            Log.d("Demo","??????????????????: " + itemPictPos);

                            db.collection("Item")
                                    .document(itemPictPos)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Get.this, "??????", Toast.LENGTH_LONG).show();


                                            Log.d("Demo", "Item????????????: " + itemPictPos);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Get.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                            storageReference.child("/Station" + staID).child(itemPictPos)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Log.d("Demo", "??????????????????item?????????: " + itemPictPos);
                                            Log.d("Demo", "??????????????????");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                            Get.this.finish();
                            Log.d("Demo", "????????????");
                        }
                    });
                    ad.setNegativeButton("???", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {

                        }
                    });
                    ad.show();
                }
            }
        });


    }

    void loadItem() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Get.this, android.R.layout.simple_list_item_1, items);

        items.clear();
        itemID.clear();

        db.collection("Item")
                .whereEqualTo("located", staID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int num = 1;

//                            String data = "";
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                Member m = doc.toObject(Member.class);
                                items.add(String.valueOf(num) + "," + m.name + "," + m.snippet);
                                itemID.add(m.ItemID);

                                Log.d("Demo", items.toString());
                                Log.d("Demo", itemID.toString());

                                num++;
                            }

                            binding.listView.setAdapter(arrayAdapter);
                        } else {
                            Log.d("Demo", "???????????????????????????");
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("Demo", e.getMessage());
                    }
                });
    }
}