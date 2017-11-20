package com.example.shiningtechw.fusedlocationtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by BiancaEn on 2017/11/16.
 */

public class FusedLocation {
    private Activity mActivity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest = new LocationRequest();
    private LocationSettingsRequest.Builder settingRequestBuilder = new LocationSettingsRequest.Builder();


    private LocationSettingsStates locationSettingsStates;
    public final static int REQUEST_CHECK_SETTING = 123;
    private Location mLocation;
    private LocationManager mLocationManager;
    private PackageManager mPackageManager;
    private long startTime, endTime;
    private double aDoubleLatitude, aDoubleLongitude;
    private int warringCount = 0;
    private String provider;

    public FusedLocation(Activity ttActivity) {
        this.mActivity = ttActivity;
        mPackageManager = ttActivity.getPackageManager();
        mLocationManager = (LocationManager) ttActivity.getSystemService(Context.LOCATION_SERVICE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        settingsClient = LocationServices.getSettingsClient(mActivity);
    }


    public void createLocationRequest(long intervalTime, long fastTime, @PriorityDefine.PriorityType int type) {

        locationRequest.setInterval(intervalTime);
        locationRequest.setFastestInterval(fastTime);
        locationRequest.setPriority(type);

        settingRequestBuilder.addLocationRequest(locationRequest);
        locationSettingsRequest = settingRequestBuilder.build();

        createLocationCallback();
    }

    private OnSuccessListener<LocationSettingsResponse> checkLocationSettingsSuccess() {
        return new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingResponse) {
                Log.v("ppking", "OnSuccess  ");
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        };
    }

    private OnFailureListener checkLocationSettingsFailure() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("ppking", "checkLocationSettingsFailure ");
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if (isAccuracyNoUse()) {
                            break;
                        }
                        //將狀態丟到系統，並由系統解決目前的問題，GPS未開啟->要求開啟，不需到設定內設定
                        ResolvableApiException rae = (ResolvableApiException) e;
                        try {
                            rae.startResolutionForResult(mActivity, REQUEST_CHECK_SETTING);
                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Toast.makeText(mActivity, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(mActivity, checkLocationSettingsSuccess())
                .addOnFailureListener(mActivity, checkLocationSettingsFailure());
    }

    private void changeAnotherAccuracy() {
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        settingRequestBuilder.addLocationRequest(locationRequest);
        locationSettingsRequest = settingRequestBuilder.build();
    }

    private boolean isAccuracyNoUse() {
        //前三次判斷GPS，之後第四次切換到其他精確度，再確認三次取消後，不繼續做詢問動作並執行destroy方法
        warringCount++;
        if (warringCount < 3 && locationRequest.getPriority() == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            Toast.makeText(mActivity, "請打開GPS定位!", Toast.LENGTH_SHORT).show();
            return false;

        } else if (warringCount >= 3 && locationRequest.getPriority() == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            changeAnotherAccuracy();
            Toast.makeText(mActivity, "改由使用WIFI網路定位\n請確認WIFI網路是否開啟", Toast.LENGTH_LONG).show();
            return false;

        } else if (warringCount > 6 && locationRequest.getPriority() == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) {
            Toast.makeText(mActivity, "目前無法定位\n請檢查WIFI網路以及GPS是否正常", Toast.LENGTH_LONG).show();
            destroy();
            return true;
        }
        return false;
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLocation = locationResult.getLastLocation();
                Log.v("ppking" , "aDoubleLatitude " + mLocation.getLatitude());
                Log.v("ppking" , "aDoubleLongitude " + mLocation.getLongitude());
                aDoubleLatitude = mLocation.getLatitude();
                aDoubleLongitude = mLocation.getLongitude();

                executeFusedCallback(aDoubleLatitude, aDoubleLongitude ,checkProvider());

            }

            //當前選擇的位置判斷是否可運作
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {

                super.onLocationAvailability(locationAvailability);
                Log.v("ppking", "onLocationAvailability ! " + locationAvailability.isLocationAvailable());
                if (!locationAvailability.isLocationAvailable()) {
                    startTime = System.currentTimeMillis();
                    Toast.makeText(mActivity, "目前定位還未成功，請稍後", Toast.LENGTH_SHORT).show();

                } else {
                    if (startTime != 0) {
                        endTime = System.currentTimeMillis();
                        long totTime = endTime - startTime;
                        executeChangeAccuracyTime(totTime);
                        Toast.makeText(mActivity, "定位成功,花費時間為 : " + totTime / 1000 + " 秒", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };
    }

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

    public void destroy() {
        warringCount = 0;
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
    }

    private List<FusedCallback> fusedList = new ArrayList<>();

    public void setFusedCallback(FusedCallback callback) {
        fusedList.add(callback);
    }

    public interface FusedCallback {
        void getLocation(double latitude, double longitude , String provider);
        void getChangeAccuracyTime(long time);
    }

    private void executeFusedCallback(double latitude, double longitude , String provider) {

        for (FusedCallback fusedCallback : fusedList
                ) {
            if (fusedCallback != null) {
                fusedCallback.getLocation(latitude, longitude , provider);
            }
        }
    }

    private void executeChangeAccuracyTime(long time){
        for (FusedCallback fusedCallback : fusedList
                ) {
            if (fusedCallback != null) {
                fusedCallback.getChangeAccuracyTime(time);
            }
        }
    }
}
