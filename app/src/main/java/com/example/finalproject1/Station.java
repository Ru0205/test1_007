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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject1.databinding.ActivityMainBinding;
import com.example.finalproject1.databinding.ActivityStationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Station extends AppCompatActivity {

    private ActivityStationBinding binding;

    private StorageReference storageReference =
            FirebaseStorage.getInstance().getReference();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String staID;
    String ann;

    List<String> item = new ArrayList<>();
    List<String> itemID = new ArrayList<>();
    Map<String, Object> a = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_station);

        binding = ActivityStationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent it = getIntent();
        staID = it.getStringExtra("Station");

        binding.txtSta.setText(staID + "???");

        Log.d("Demo", "staID: " + staID);

        //???????????????txtAnn
        db.collection("Station")
                .document("Station" + staID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Member m = documentSnapshot.toObject(Member.class);

                        Log.d("Demo", "???????????????");

                        a.put("ann", m.ann);
                        a.put("id", m.id);
                        a.put("snippet", m.snippet);
                        a.put("title", m.title);
                        a.put("geo", m.geo);

                        Log.d("Demo", a.toString());

                        ann = m.ann;

                        if ("" .equals(ann.trim())) {
                            binding.txtAnn.setText("??????:???");
                        } else {
                            binding.txtAnn.setText("??????:" + ann);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("Demo", e.getMessage());
                    }
                });


        //???????????????alertDialog?????????????????????????????????txtAnn
        binding.btnAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Demo", "staID????????????: " + staID);

                LayoutInflater layoutInflater = getLayoutInflater();
                final View annView = layoutInflater.inflate(R.layout.ann, null);

                new AlertDialog.Builder(Station.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("??????")
                        .setMessage("????????????????????????")
                        .setView(annView)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText editTextAnn = annView.findViewById(R.id.editTextAnn);
                                binding.txtAnn.setText("??????:" + editTextAnn.getText().toString());

                                a.put("ann", editTextAnn.getText().toString());

                                Log.d("Demo", "???????????????" + editTextAnn.getText().toString());

                                db.collection("Station")
                                        .document("Station" + staID)
                                        .set(a)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(Station.this, "????????????" + editTextAnn.getText().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Station.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });


                            }
                        }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Station.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });


        //???????????????????????????Donate???????????????staID
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Station.this, Donate.class);
                it.putExtra("Station", staID);
                startActivity(it);
            }
        });

        //??????????????
        binding.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(Station.this, Get.class);
                it.putExtra("Station", staID);
                it.putExtra("Status","manager");
                startActivity(it);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Demo", "Station onPause");
    }


    //???onStart?????????????????????(??????????????????????????????)?????????listView
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Demo", "Station onStart");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Station.this, android.R.layout.simple_list_item_1, item);

        item.clear();
        itemID.clear();
        binding.imageView.setImageBitmap(null);

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
                                item.add(String.valueOf(num) + "," + m.name + "," + m.snippet);
                                itemID.add(m.ItemID);

                                Log.d("Demo", item.toString());
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

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("Demo", "?????????id: " + itemID.get(i));

                storageReference.child("/Station" + staID).child(itemID.get(i))
                        .getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Glide.with(Station.this)
                                        .load(uri)
                                        .into(binding.imageView);

                                Log.d("Demo", itemID.get(i) + "?????????" + uri);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Station.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Demo", "Station onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Demo", "Station onResume");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//???????????????
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//????????????????????????????????????
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void ConfirmExit() {//????????????

        AlertDialog.Builder ad = new AlertDialog.Builder(Station.this);
        ad.setTitle("??????");
        ad.setMessage("???????????????????");
        ad.setPositiveButton("???", new DialogInterface.OnClickListener() {//????????????
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub

                Intent it = new Intent(Station.this, Home.class);
                startActivity(it);
                Station.this.finish();

                Log.d("Demo", "???????????????");

            }
        });
        ad.setNegativeButton("???", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //?????????????????????????????????
            }
        });
        ad.show();//???????????????
    }
}