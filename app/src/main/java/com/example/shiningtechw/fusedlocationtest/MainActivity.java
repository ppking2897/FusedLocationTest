package com.example.shiningtechw.fusedlocationtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_PERMISSIONS_REQUEST_CODE = 123;
    private FusedLocationProviderClient client;
    private SettingsClient settingsClient;

    private Location location;
    private LocationRequest HIGH_ACCURACY_LocationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;


    private long UPDATE_TIME_SEC_FOR_HIGH_ACCURACY = 10000;
    private long UPDATE_TIME_SEC_FOR_BALANCED_ACCURACY = 20000;
    private long UPDATE_TIME_SEC_FOR_LOW_POWER = 30000;
    private final int REQUEST_CHECK_SETTING = 1111;

    private LocationRequest BALANCED_POWER_ACCURACY_LocationRequest;
    private LocationRequest LOW_POWER_LocationRequest;

    private LocationManager locationManager ;
    private PackageManager packageManager ;


    private TextView providerText , locationText , time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        providerText = findViewById(R.id.provider);
        locationText = findViewById(R.id.location);
        time = findViewById(R.id.time);

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        packageManager = this.getPackageManager();


        //Client 建構
        client = LocationServices.getFusedLocationProviderClient(this);
        //settingsClient 設定依時間取得座標
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback();
        createHIGHLocationRequest();

        builderHIGH_ACCURACYLocationSettingRequest();
        startLocationUpdates();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()){
            requestPermissions();
        }

    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(){
        client.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()&&task.getResult()!=null){
                    location = task.getResult();

                    Log.v("ppking" , "getLastLocation location.getLatitude() : " + location.getLatitude());
                    Log.v("ppking" , "getLastLocation location.getLongitude() : " + location.getLongitude());
                }else{
                    Log.v("ppking" , "getLastLocation:exception  :"+ task.getException());
                }
            }
        });
    }

    private void createHIGHLocationRequest(){
        HIGH_ACCURACY_LocationRequest = new LocationRequest();
        HIGH_ACCURACY_LocationRequest.setInterval(UPDATE_TIME_SEC_FOR_HIGH_ACCURACY);
        HIGH_ACCURACY_LocationRequest.setFastestInterval(UPDATE_TIME_SEC_FOR_HIGH_ACCURACY /2);
        HIGH_ACCURACY_LocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createBALENCELocationRequest(){
        BALANCED_POWER_ACCURACY_LocationRequest = new LocationRequest();
        BALANCED_POWER_ACCURACY_LocationRequest.setInterval(UPDATE_TIME_SEC_FOR_BALANCED_ACCURACY);
        BALANCED_POWER_ACCURACY_LocationRequest.setFastestInterval(UPDATE_TIME_SEC_FOR_BALANCED_ACCURACY /2);
        BALANCED_POWER_ACCURACY_LocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void createLOWPOWERLocationRequest(){
        LOW_POWER_LocationRequest = new LocationRequest();
        LOW_POWER_LocationRequest.setInterval(UPDATE_TIME_SEC_FOR_LOW_POWER);
        LOW_POWER_LocationRequest.setFastestInterval(UPDATE_TIME_SEC_FOR_LOW_POWER /2);
        LOW_POWER_LocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    private void createLocationCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                location = locationResult.getLastLocation();

                Log.v("ppking" , "LocationCallBack location.getLatitude() : " + location.getLatitude());
                Log.v("ppking" , "LocationCallBack location.getLongitude() : " + location.getLongitude());

                checkProvider();

                locationText.append(""+location.getLatitude() + "  " + location.getLongitude() +"\n");


            }

            //當前選擇的位置判斷是否可運作
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.v("ppking" , "onLocationAvailability ! "  + locationAvailability.toString());
                if (!locationAvailability.isLocationAvailable()){
                    Toast.makeText(MainActivity.this , providerText + "無法定位，正在切換定位方法" , Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void startLocationUpdates(){
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        client.requestLocationUpdates(HIGH_ACCURACY_LocationRequest, locationCallback, Looper.myLooper());
                        Log.v("ppking" , "onSuccess !!");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("ppking" , " onFailure !!" + e.toString());
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("ppking", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTING);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("ppking", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("ppking", errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    //設定位置精確度
    private void builderHIGH_ACCURACYLocationSettingRequest(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(HIGH_ACCURACY_LocationRequest);
        locationSettingsRequest = builder.build();
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
                startLocationUpdates();
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
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.v("ppking" , "RESULT_CANCELED !!");
                        Toast.makeText(MainActivity.this , "請打開GPS定位!" , Toast.LENGTH_SHORT).show();
                        //startLocationUpdates();
                        break;
                }
                break;

        }
    }

    private void checkProvider(){
        boolean gpsUsable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean gpsPresent = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        boolean networkUsable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean networkPresent =
                packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);

//        boolean bleUsable = networkUsable;
//        boolean blePresent = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);


        if (gpsPresent&&gpsUsable){
            providerText.setText("GPS Provider\n");
        }else if (networkPresent&&networkUsable){
            providerText.setText("NET Provider\n");
        }else {
            providerText.append("no Provider\n");
            Toast.makeText(this , "定位失敗，請檢查設備" , Toast.LENGTH_SHORT).show();
        }


        String str = DateFormat.getDateTimeInstance().format(new Date());

        time.setText("經緯度 : \n "+str);
//        Log.v("ppking"  , " checkProvider bleUsable : "+bleUsable);
//        Log.v("ppking"  , " checkProvider blePresent : "+blePresent);
    }
}
