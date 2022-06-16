package com.example.finalproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject1.databinding.ActivityHomeBinding;
import com.example.finalproject1.databinding.ActivityHomeUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Maps;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HomeUser extends AppCompatActivity {

    private ActivityHomeUserBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String staID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home_user);

        binding = ActivityHomeUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent it = getIntent();
        staID = it.getStringExtra("Station");

//        binding.imageView.setImageBitmap();

        binding.txtSta.setText("愛心物資站 (" + staID + "站)");

        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/finalproject-6545d.appspot.com/o/%E7%AB%99%E9%BB%9Eqrcode%2Fphoto_360624.jpeg?alt=media&token=9a61a523-90f2-47ac-90d0-deb95a7e3292";

        Glide.with(HomeUser.this)
                .load(imgUrl)
                .into(binding.imageView);

        checkAnn();
        //彈出公告視窗
        Log.d("Demo", "staID是: " + staID);

        binding.btnAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkAnn();
            }
        });

        binding.btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(HomeUser.this,QRPage.class);
                startActivity(it);

            }
        });

        binding.btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(HomeUser.this, Get.class);
                it.putExtra("Station", staID);
                it.putExtra("Status","user");
                startActivity(it);

                Log.d("Demo","開啟Get畫面，並傳送staID: "+staID);
                Log.d("Demo","開啟Get畫面，並傳送status");

            }
        });

        binding.btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomeUser.this, Donate.class);
                it.putExtra("Station", staID);
                startActivity(it);
            }
        });

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomeUser.this, MapsActivity.class);
                it.putExtra("Station", staID);
                startActivity(it);
                finish();

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void ConfirmExit() {//退出確認

        AlertDialog.Builder ad = new AlertDialog.Builder(HomeUser.this);
        ad.setTitle("離開");
        ad.setMessage("確定要回到主畫面嗎?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub

                HomeUser.this.finish();

                Log.d("Demo", "回主畫面");
            }
        });
        ad.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//顯示對話框
    }

    public void checkAnn() {

        if ("".equals(staID.trim())) {

        } else {
            Log.d("Demo", "staID不是空值: " + staID);
            db.collection("Station")
                    .document("Station" + staID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Member m = documentSnapshot.toObject(Member.class);

                            Log.d("Demo", "進成功畫面");

                            String ann = m.ann;

                            if ("".equals(ann.trim())) {

                                Toast.makeText(HomeUser.this,"暫無公告哦!",Toast.LENGTH_LONG).show();

                            } else {

                                Log.d("Demo", "script是:" + ann);

                                AlertDialog.Builder announcement = new AlertDialog.Builder(HomeUser.this);
                                announcement.setTitle("公告");
                                announcement.setMessage(ann);
                                announcement.setPositiveButton("收到", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                announcement.show();

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
}