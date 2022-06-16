package com.example.finalproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.finalproject1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();


        //我們三個的學號都有建立,密碼都是123456
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String mail = "4a890007@stust.edu.tw";
               String mail = binding.editTextEmail.getText().toString();
                //String pwd = "123456";
               String pwd = binding.editTextPwd.getText().toString();

                if("".equals(mail.trim()) || "".equals(pwd.trim()))
                {
                    Toast.makeText(MainActivity.this,"請輸入信箱及密碼",Toast.LENGTH_LONG).show();
                }else
                {
                    mAuth.signInWithEmailAndPassword(mail, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(MainActivity.this, "登入成功:" + task.getResult().getUser().getEmail()
                                                , Toast.LENGTH_LONG).show();

                                        checkUser();

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(MainActivity.this, "輸入失敗" + e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                }

            }
        });

        //離開按鈕，確認是否離開
        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConfirmExit();
            }
        });

        binding.btnPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = binding.editTextEmail.getText().toString();

                if("".equals(mail.trim())){
                    Toast.makeText(MainActivity.this,"請輸入信箱",Toast.LENGTH_LONG).show();

                }else {
                    mAuth.sendPasswordResetEmail(binding.editTextEmail.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "已傳送密碼更新連結至信箱", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        //顯示密碼
        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    binding.editTextPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    binding.editTextPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        binding.btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, MapsActivity.class);
                it.putExtra("Station","");
                startActivity(it);
                Log.d("Demo","切換到map畫面");
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
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開此程式嗎?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub

                MainActivity.this.finish();//關閉activity
                Log.d("Demo","關閉App");
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//顯示對話框
    }

    void checkUser() {
        if (mAuth.getCurrentUser() != null) {

            Intent it = new Intent(MainActivity.this, Home.class);
            startActivity(it);

        } else {
            Log.d("Demo", "失敗");
        }
    }

}