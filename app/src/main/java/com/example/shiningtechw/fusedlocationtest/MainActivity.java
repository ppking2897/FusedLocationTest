package com.example.shiningtechw.fusedlocationtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.whichProvider_0)
    TextView whichProvider0;
    @BindView(R.id.locationTimeGet)
    TextView locationTimeGet;
    @BindView(R.id.changeAccuracyTime)
    TextView changeAccuracyTime;
    @BindView(R.id.GPS_Swich)
    Switch GPSSwich;
    @BindView(R.id.finish_location)
    Switch finishLocation;
    @BindView(R.id.setIntervalTime)
    Button setIntervalTime;
    @BindView(R.id.returnIntervalTime)
    EditText returnIntervalTime;

    private int REQUEST_PERMISSIONS_REQUEST_CODE = 1111;
    private final int REQUEST_CHECK_SETTING = 123;
    private int intervalTime = 10000;
    private int fastTime = 5000;

    private FusedLocation mGPSFusedLocation;

    private List<String> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getGPSLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGPSFusedLocation.destroy();
    }

    //權限要求
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "請允許權限,否則無法定位", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    //丟出未開啟的狀態，選擇確定或取消後的動作選擇
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTING:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.v("ppking", "RESULT_OK !!");
                        getGPSLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.v("ppking", "RESULT_CANCELED !!");
                        mGPSFusedLocation.startLocationUpdates();
                        break;
                }
                break;

        }
    }

    public void getGPSLocation() {
        mGPSFusedLocation = new FusedLocation(this);
        mGPSFusedLocation.createLocationRequest(intervalTime, fastTime, PriorityDefine.PRIORITY_HIGH_ACCURACY);
        mGPSFusedLocation.startLocationUpdates();
        mGPSFusedLocation.setFusedCallback(new FusedLocation.FusedCallback() {
            @Override
            public void getLocation(double latitude, double longitude, String provider) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                Date date = new Date();
                whichProvider0.setText(provider);

                lists.add(0, dateFormat.format(date) + "\n" + latitude + "    " + longitude + "\n");
                locationTimeGet.setText("");

                for (int i = 0; i < lists.size(); i++) {
                    locationTimeGet.append(String.format("%s", lists.get(i) + "\n"));
                }
            }

            @Override
            public void getChangeAccuracyTime(long time) {
                changeAccuracyTime.setText(String.format("%s秒", String.valueOf(time / 1000)));
            }
        });
    }


    @OnClick({R.id.GPS_Swich, R.id.finish_location, R.id.setIntervalTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.GPS_Swich:
                break;
            case R.id.finish_location:
                if (finishLocation.isChecked()) {
                    mGPSFusedLocation.destroy();
                    mGPSFusedLocation = null;
                } else if (!finishLocation.isChecked()) {
                    getGPSLocation();
                }
                break;
            case R.id.setIntervalTime:
                int time = Integer.parseInt(returnIntervalTime.getText().toString());
                if (time!=0){
                    if (mGPSFusedLocation == null){
                        getGPSLocation();
                    }else{
                        mGPSFusedLocation.destroy();
                        getGPSLocation();
                    }
                }
        }
    }
//    public void setTime(View view) {
//
//        if (Integer.parseInt(setIntervalTime.getText().toString()) !=0){
//
//        }
//                if (mGPSFusedLocation == null){
//                    getGPSLocation();
//                }
//    }
}
