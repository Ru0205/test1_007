package com.example.finalproject1;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.finalproject1.databinding.ActivityHomeBinding;
import com.example.finalproject1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference =
            FirebaseStorage.getInstance().getReference();

    List<String> StationID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadStation();

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Demo", String.valueOf(position));

                Intent it = new Intent(Home.this, Station.class);
                it.putExtra("Station", StationID.get(position));
                Log.d("Demo", StationID.get(position));
                startActivity(it);
                Home.this.finish();

            }
        });


        //長按刪除功能
        binding.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                new AlertDialog.Builder(Home.this)
                        .setTitle("詢問視窗")
                        .setMessage("是否確定要刪除站點?")
                        .setIcon(R.mipmap.ic_launcher)

                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {

                                //刪除對應的站別
                                db.collection("Station")
                                        .document("Station" + StationID.get(i))
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Demo","刪除對應站點"+StationID.get(i));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                Log.d("Demo","第一階段完成");

                                //刪除圖片庫
//                                String Folder = "/Station" + StationID.get(i);
//                                Log.d("Demo","文件夾名稱" + Folder);
//
//                                storageReference.child(Folder)
//                                        .delete()
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//
//                                                Log.d("Demo", "圖庫刪除成功");
//
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//
//                                            }
//                                        });

                                //刪除對應站別的Item
                                db.collection("Item")
                                        .whereEqualTo("located",StationID.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                                        String itemID = documentSnapshot.getId();

                                                        Log.d("Demo", documentSnapshot.getId() + documentSnapshot.getData());

                                                        db.collection("Item")
                                                                .document(itemID)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                            Log.d("Demo","對應單筆Item資料刪除成功" + itemID);

                                                                        //刪除對應站別的圖庫
                                                                        storageReference.child("/Station" + StationID.get(i)).child(itemID)
                                                                                .delete()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                        Log.d("Demo","刪除單筆對應item的圖片: " + itemID);
                                                                                        Log.d("Demo", "全部刪除成功");

                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                    }
                                                                                });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d("Demo",e.getMessage());
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Demo", e.getMessage());
                                            }
                                        });



                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Home.this, "取消執行刪除", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNeutralButton("再想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Home.this, "並未執行刪除", Toast.LENGTH_LONG).show();
                            }
                        })

                        .show();

                return true;
            }

        });

        //新增站點
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = getLayoutInflater();
                final View station_add = layoutInflater.inflate(R.layout.station_add, null);

                new AlertDialog.Builder(Home.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("請輸入站名")
                        .setMessage("ex:D")
                        .setView(station_add)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText staID = station_add.findViewById(R.id.staID);
                                EditText staSnippet = station_add.findViewById(R.id.staSnippet);
                                EditText staGeox = station_add.findViewById(R.id.staGeox);
                                EditText staGeoy = station_add.findViewById(R.id.staGeoy);

                                Map<String, Object> station = new HashMap<>();

                                station.put("id", staID.getText().toString());
                                station.put("title", staID.getText().toString() + "站");
                                station.put("snippet", staSnippet.getText().toString());
                                station.put("ann", "");
                                double x = Double.parseDouble(staGeox.getText().toString());
                                double y = Double.parseDouble(staGeoy.getText().toString());

                                station.put("geo", new GeoPoint(x, y));

                                String key = staID.getText().toString();
                                Log.d("Demo", "抓staName: " + key);

                                db.collection("Station")
                                        .document("Station" + key)
                                        .set(station)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Home.this, "新增成功", Toast.LENGTH_LONG).show();

                                                loadStation();
                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Home.this, "取消新增作業", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();

            }
        });

    }

    void loadStation() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Home.this, android.R.layout.simple_list_item_1, StationID);

        StationID.clear();

        db.collection("Station")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Member m = documentSnapshot.toObject(Member.class);

                                StationID.add(m.id);
                                Log.d("Demo", "add的部分" + m.id);

                            }

                            binding.listView.setAdapter(arrayAdapter);
                            Log.d("Demo", "StationID的部分" + StationID.toString());

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Demo", e.getMessage());
                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Demo","Home onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Demo","Home onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Demo","Home onDestroy");
    }
}