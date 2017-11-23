package com.example.shiningtechw.fusedlocationtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by BiancaEn on 2017/11/16.
 *
 * 與Activity內的onActivityResult作結果之後的事件觸發
 */

public class FusedLocationImp implements FusedLocation  {
    private Activity mActivity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest = new LocationRequest();
    private LocationSettingsRequest.Builder settingRequestBuilder = new LocationSettingsRequest.Builder();

    public final static int REQUEST_CHECK_SETTING = 123;
    private Location mLocation;
    private LocationManager mLocationManager;
    private PackageManager mPackageManager;
    private long startTime, endTime;
    private double aDoubleLatitude, aDoubleLongitude;
    private String provider;
    private int getLocationCount;
    private Timer lostGPSLocationTimer;


    public FusedLocationImp (Activity activity){
        this.mActivity = activity;
        mPackageManager = activity.getPackageManager();
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        settingsClient = LocationServices.getSettingsClient(mActivity);

    }

    private OnSuccessListener<LocationSettingsResponse> checkLocationSettingsSuccess() {
        return new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingResponse) {
                Log.v("ppking", "OnSuccess  ");
                LocationSettingsStates locationSettingsStates = locationSettingResponse.getLocationSettingsStates();
                boolean gpsUsable = locationSettingsStates.isGpsUsable();
                boolean gpsPresent = locationSettingsStates.isGpsPresent();

                Log.v("ppking", "gpsUsable  " + gpsUsable );
                Log.v("ppking", "gpsPresent  " + gpsPresent );

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }
        };
    }

    private OnFailureListener checkLocationSettingsFailure() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.v("ppking", "checkLocationSettingsFailure ");
//                int statusCode = ((ApiException) e).getStatusCode();
//                switch (statusCode) {
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        //將狀態丟到系統，並由系統解決目前的問題，GPS未開啟->要求開啟，不需到設定內設定
//                        ResolvableApiException rae = (ResolvableApiException) e;
//                        try {
//                            rae.startResolutionForResult(mActivity, REQUEST_CHECK_SETTING);
//                        } catch (IntentSender.SendIntentException e1) {
//                            e1.printStackTrace();
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        String errorMessage = "Location settings are inadequate, and cannot be " +
//                                "fixed here. Fix in Settings.";
//                        Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
//                }
            }
        };
    }
    @Override
    public void startLocationUpdates() {
        if (lostGPSLocationTimer == null){
            lostGPSLocationTimer = new Timer();
            lostGPSLocationTimer.schedule(new TaskNetProvider() , 0 , 1000);
        }
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(mActivity, checkLocationSettingsSuccess())
                .addOnFailureListener(mActivity, checkLocationSettingsFailure());
    }

    @Override
    public void createLocationRequest(long intervalTime, long fastTime, int type) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(intervalTime);
        locationRequest.setFastestInterval(fastTime);
        locationRequest.setPriority(type);
        //設置存在多久時間
        //locationRequest.setExpirationDuration(30000);

        //還不確定
        //locationRequest.setExpirationTime(30000);
        //回傳幾次經緯度就終止
        //locationRequest.setNumUpdates(5);

        settingRequestBuilder.addLocationRequest(locationRequest);

        locationSettingsRequest = settingRequestBuilder.build();

        createLocationCallback();
    }

    @Override
    public void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLocation = locationResult.getLastLocation();
                Log.v("ppking" , "aDoubleLatitude " + mLocation.getLatitude());
                Log.v("ppking" , "aDoubleLongitude " + mLocation.getLongitude());
                aDoubleLatitude = mLocation.getLatitude();
                aDoubleLongitude = mLocation.getLongitude();

                getLocationCount=0;

                executeFusedCallback(aDoubleLatitude, aDoubleLongitude ,checkProvider());

            }

            //當前選擇的位置判斷是否可運作
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {

                super.onLocationAvailability(locationAvailability);
                Log.v("ppking", "onLocationAvailability ! " + locationAvailability.isLocationAvailable());
                if (!locationAvailability.isLocationAvailable()) {
                    startTime = System.currentTimeMillis();
                    executeChangeAccuracyMessage("目前定位還未成功，請稍後");

                } else {
                    if (startTime != 0) {
                        endTime = System.currentTimeMillis();
                        long totTime = endTime - startTime;
                        executeChangeAccuracyMessage("定位成功,花費時間為 : " + totTime / 1000 + " 秒");
                    }

                }
            }
        };
    }

    @Override
    public String checkProvider() {
//        boolean gpsUsable = locationSettingsStates.isGpsUsable();
//        boolean gpsPresent = locationSettingsStates.isGpsPresent();
//
//        boolean networkUsable = locationSettingsStates.isNetworkLocationUsable();
//        boolean networkPresent = locationSettingsStates.isNetworkLocationPresent();

        boolean gpsUsable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean gpsPresent = mPackageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        boolean networkUsable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean networkPresent =
                mPackageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);

        if (gpsPresent && gpsUsable) {
            Log.v("ppking", "GPS PROVIDER");
            provider = "GPS";
        } else if (networkPresent && networkUsable) {
            Log.v("ppking", "NET PROVIDER");
            provider = "NET";
        } else {
            Log.v("ppking", "NO PROVIDER!!");
            provider = "None";
        }
        return provider;
    }

    private List<FusedCallback> fusedList = new ArrayList<>();

    @Override
    public void setFusedCallback(FusedLocation.FusedCallback callback) {
        fusedList.add(callback);
    }

    @Override
    public void destroy() {
        fusedList.clear();
        locationSettingsRequest = null;
        settingRequestBuilder =null;
        locationRequest = null;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        fusedLocationProviderClient = null;
        settingsClient = null;
        mPackageManager = null;
        mLocationManager = null;
        locationCallback = null;
        mLocation = null;
        if (lostGPSLocationTimer !=null){
            lostGPSLocationTimer.cancel();
            lostGPSLocationTimer = null;
        }
    }

    private void changeAnotherAccuracy(@PriorityDefine.PriorityType int type) {
        locationRequest.setPriority(type);
        settingRequestBuilder.addLocationRequest(locationRequest);
        locationSettingsRequest = settingRequestBuilder.build();
    }

    private void executeFusedCallback(double latitude, double longitude , String provider) {

        for (FusedCallback fusedCallback : fusedList
                ) {
            if (fusedCallback != null) {
                fusedCallback.getLocation(latitude, longitude , provider);
            }
        }
    }

    private void executeChangeAccuracyMessage(String message){
        for (FusedCallback fusedCallback : fusedList
                ) {
            if (fusedCallback != null) {
                fusedCallback.getChangeAccuracyMessage(message);
            }
        }
    }

    private class TaskNetProvider extends TimerTask{

        @Override
        public void run() {
            getLocationCount+=1;
            Log.v("ppking" , "getLocationCount " + getLocationCount);
            if (getLocationCount >=20){
                changeAnotherAccuracy(PriorityDefine.PRIORITY_BALANCED_POWER_ACCURACY);
                startLocationUpdates();
                lostGPSLocationTimer.cancel();
                lostGPSLocationTimer = null;
            }
        }
    }
}
