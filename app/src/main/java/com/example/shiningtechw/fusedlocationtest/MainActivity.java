package com.example.shiningtechw.fusedlocationtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{

    private int REQUEST_PERMISSIONS_REQUEST_CODE = 1111;
    private final int REQUEST_CHECK_SETTING = 123;

    private TextView gpsProvider;
    private FusedLocation fusedLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocation = new FusedLocation(this);
        fusedLocation.createLocationRequest(5000 , 5000 , 100);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()){
            requestPermissions();
        }
    }

    //權限要求
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults){
            if (grantResult != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this , "請允許權限,否則無法定位" , Toast.LENGTH_SHORT).show();
            }else{

            }
        }
    }

    //丟出未開啟的狀態，選擇確定或取消後的動作選擇
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CHECK_SETTING :
                switch (resultCode){
                    case Activity.RESULT_OK:
                        Log.v("ppking" , "RESULT_OK !!");

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.v("ppking" , "RESULT_CANCELED !!");
                        fusedLocation.startLocationUpdates();
                        break;
                }
                break;

        }
    }

}
