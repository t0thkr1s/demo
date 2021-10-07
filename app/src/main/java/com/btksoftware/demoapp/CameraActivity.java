package com.btksoftware.demoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    CameraSource cameraSource;
    SurfaceView surfaceView;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_camera);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        textRecognizer = new TextRecognizer.Builder(this).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build();

        surfaceView = findViewById(R.id.surface_camera_preview);
        surfaceView.getHolder().addCallback(this);

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                if (sparseArray.size() != 0) {
                    for (int i = 0; i < sparseArray.size(); i++) {
                        TextBlock textBlock = sparseArray.get(i);
                        String detection = textBlock.getValue();
                        Log.d("APP", detection);
                        if (detection.contains("Flóraszept")) {
                            textRecognizer.release();
                            Log.d("APP", "INSIDE:" + detection);
                            // SHOW DETAILS VIEW
                            Intent details = new Intent(CameraActivity.this, DetailsActivity.class);
                            details.putExtra("item", "Flóraszept");
                            startActivity(details);
                        }
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        textRecognizer = new TextRecognizer.Builder(this).build();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                if (sparseArray.size() != 0) {
                    for (int i = 0; i < sparseArray.size(); i++) {
                        TextBlock textBlock = sparseArray.get(i);
                        String detection = textBlock.getValue();
                        Log.d("APP", detection);
                        if (detection.contains("Flóraszept")) {
                            textRecognizer.release();
                            Log.d("APP", "INSIDE:" + detection);
                            // SHOW DETAILS VIEW
                            Intent details = new Intent(CameraActivity.this, DetailsActivity.class);
                            details.putExtra("item", "Flóraszept");
                            startActivity(details);
                        }
                    }
                }
            }
        });
    }

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("APP", "Camera permission granted!");
            } else {
                Log.d("APP", "Camera permission denied!");
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            if (isCameraPermissionGranted()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(surfaceView.getHolder());
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                }
            }
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
}