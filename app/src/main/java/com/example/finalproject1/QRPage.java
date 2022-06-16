package com.example.finalproject1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.finalproject1.databinding.ActivityQrpageBinding;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


public class QRPage extends AppCompatActivity {

    private ActivityQrpageBinding binding;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_qrpage);

        binding = ActivityQrpageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("QRCode掃描器");

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource=new CameraSource.Builder(this,barcodeDetector)
                .setRequestedPreviewSize(300,300)
                .setAutoFocusEnabled(true).build();


        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                    return;

                try {
                    cameraSource.start(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        //偵測QRCode觸發事件
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            //在觸發事件中撰寫讀取程式
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    binding.textView.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.textView.setText(qrCodes.valueAt(0).displayValue);

                            new AlertDialog.Builder(QRPage.this)
                                    .setTitle("要前往嗎？")
                                    .setMessage(qrCodes.valueAt(0).displayValue)
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String url = qrCodes.valueAt(0).displayValue;

                                            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(it);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("不是",null)
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }
            }
        });

    }
}

