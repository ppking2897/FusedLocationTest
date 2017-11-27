package com.example.shiningtechw.fusedlocationtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

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
    @BindView(R.id.GPS_Switch)
    Switch GPSSwitch;
    @BindView(R.id.finish_location)
    Switch finishLocation;

    @BindView(R.id.returnIntervalTime)
    EditText returnIntervalTime;

    private final int REQUEST_CHECK_SETTING = 123;
    @BindView(R.id.setHighAccuracy)
    Button setHighAccuracy;
    @BindView(R.id.setBalanceAccuracy)
    Button setBalanceAccuracy;
    @BindView(R.id.setLowAccuracy)
    Button setLowAccuracy;

    private FusedLocationImp mGPSFusedLocation;

    private List<String> lists = new ArrayList<>();

    private ToastHelper mToastHelper = new ToastHelper(this);
    private LocationManager mLocationManager;
    private PackageManager mPackageManager;
    private PermissionCheckImp permissionCheckImp;

    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private int REQUEST_PERMISSIONS_REQUEST_CODE = 1111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkGPSUsable();
        permissionCheckImp = new PermissionCheckImp(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGPSFusedLocation.destroy();
    }

    //權限結果事件觸發
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                permissionCheckImp.resultCallback(false);
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
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.v("ppking", "RESULT_CANCELED !!");
                        break;
                }
                break;

        }
    }

    public void getLocation() {
        mGPSFusedLocation.setFusedCallback(new FusedLocation.FusedCallback() {
            @Override
            public void getLocation(double latitude, double longitude, String provider) {
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                Date date = new Date();

                if (provider.equals("100")){
                    whichProvider0.setText("高精確度");
                }else if (provider.equals("102")){
                    whichProvider0.setText("省電精確度");
                }


                if (provider.equals("GPS")) {
                    GPSSwitch.setChecked(true);
                } else {
                    GPSSwitch.setChecked(false);
                }

                lists.add(0, dateFormat.format(date) + "\n" + latitude + "    " + longitude + "\n");
                locationTimeGet.setText("");

                for (int i = 0; i < lists.size(); i++) {
                    locationTimeGet.append(String.format("%s", lists.get(i) + "\n"));
                }
            }
            @Override
            public void getChangeAccuracyMessage(float speed) {
                changeAccuracyTime.append(String.valueOf(speed));
            }
        });
    }

    public void checkGPSUsable() {
        mPackageManager = this.getPackageManager();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean gpsUsable = false;
        if (mLocationManager != null) {
            gpsUsable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        boolean gpsPresent = mPackageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (gpsPresent && gpsUsable) {
            GPSSwitch.setChecked(true);
        } else {
            GPSSwitch.setChecked(false);
        }
    }

    public boolean requestPermission() {
        if (!permissionCheckImp.checkPermissions(permissions)) {
            permissionCheckImp.requestPermissions(permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
            return false;
        }

        return true;
    }

    public void setAccuracy(@PriorityDefine.PriorityType int type) {
        if (!returnIntervalTime.getText().toString().equals("")) {
            int time = Integer.parseInt(returnIntervalTime.getText().toString()) * 1000;
            if (mGPSFusedLocation != null) {
                mGPSFusedLocation.destroy();
            }
            mGPSFusedLocation = new FusedLocationImp(this);
            mGPSFusedLocation.createLocationRequest(time, time, type);
            mGPSFusedLocation.startLocationUpdates();

            getLocation();

            mToastHelper.showMyToast("設定回傳的時間為" + returnIntervalTime.getText().toString() + "秒");

            //按下button關閉鍵盤
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(returnIntervalTime.getWindowToken(), 0);
            }
            //結束定位顯示為關
            finishLocation.setChecked(false);

        } else {
            mToastHelper.showMyToast("請輸入時間,單位為秒,盡量大於5秒");
        }
    }

    @OnClick({R.id.setHighAccuracy, R.id.setBalanceAccuracy, R.id.setLowAccuracy,
            R.id.GPS_Switch, R.id.finish_location})
    public void onViewClicked(View view) {
        if (requestPermission()) {
            switch (view.getId()) {
                case R.id.GPS_Switch:
                    checkGPSUsable();
                    break;

                case R.id.finish_location:
                    if (finishLocation.isChecked()) {
                        if (mGPSFusedLocation != null) {
                            lists.clear();
                            locationTimeGet.setText("");
                            mGPSFusedLocation.destroy();
                            mGPSFusedLocation = null;
                            changeAccuracyTime.setText("");
                        }
                    }
                    break;

                case R.id.setHighAccuracy:
                    setAccuracy(PriorityDefine.PRIORITY_HIGH_ACCURACY);
                    break;

                case R.id.setBalanceAccuracy:
                    setAccuracy(PriorityDefine.PRIORITY_BALANCED_POWER_ACCURACY);
                    break;

                case R.id.setLowAccuracy:
                    setAccuracy(PriorityDefine.PRIORITY_LOW_POWER);
                    break;
            }
        }
    }
}
