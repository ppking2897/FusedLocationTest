package com.example.shiningtechw.fusedlocationtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
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
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by BiancaEn on 2017/11/16.
 *
 * 與Activity內的onActivityResult作結果之後的事件觸發
 */

public class FusedLocationImp implements FusedLocation  {
    private Activity mActivity;
    private FusedLocationProviderClient fusedLocationGpsProviderClient;

    private LocationCallback locationGpsCallback;

    private LocationRequest locationGpsRequest;

    private LocationSettingsRequest.Builder settingGpsRequestBuilder;

    private SettingsClient settingsGpsClient;

    private LocationSettingsRequest locationGpsSettingsRequest;

    public final static int REQUEST_CHECK_SETTING = 123;
    private Location mGpsLocation;
    private Location mNetLocation;

    private LocationManager mLocationManager;
    private PackageManager mPackageManager;
    private long startTime, endTime;
    private double aDoubleLatitude, aDoubleLongitude;
    private String provider;
    private int getLocationCount;
    private Timer lostGPSLocationTimer;
    private boolean isNoSignal;
    private long mIntervalTime , mFastTime;


    public FusedLocationImp (Activity activity){
        this.mActivity = activity;
        mPackageManager = activity.getPackageManager();
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        initClient();

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

                fusedLocationGpsProviderClient.requestLocationUpdates(locationGpsRequest, locationGpsCallback, Looper.myLooper());
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
    @Override
    public void startLocationUpdates() {
        if (lostGPSLocationTimer == null && !isNoSignal){
            lostGPSLocationTimer = new Timer();
            lostGPSLocationTimer.schedule(new TaskNetProvider() , 0 , 1000);
        }
        settingsGpsClient.checkLocationSettings(locationGpsSettingsRequest)
                .addOnSuccessListener(mActivity, checkLocationSettingsSuccess())
                .addOnFailureListener(mActivity, checkLocationSettingsFailure());
    }

    @Override
    public void createLocationRequest(long intervalTime, long fastTime, int type) {
        locationGpsRequest.setInterval(intervalTime);
        locationGpsRequest.setFastestInterval(fastTime);
        locationGpsRequest.setPriority(type);

        mIntervalTime =intervalTime;
        mFastTime =fastTime;

        settingGpsRequestBuilder.addLocationRequest(locationGpsRequest);
        locationGpsSettingsRequest = settingGpsRequestBuilder.build();

        createLocationCallback();
    }

    @Override
    public void createLocationCallback() {
        locationGpsCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mGpsLocation = locationResult.getLastLocation();
                Log.v("ppking" , "GpsLatitude " + mGpsLocation.getLatitude());
                Log.v("ppking" , "GpsLongitude " + mGpsLocation.getLongitude());
                aDoubleLatitude = mGpsLocation.getLatitude();
                aDoubleLongitude = mGpsLocation.getLongitude();

                getLocationCount=0;

                executeFusedCallback(aDoubleLatitude, aDoubleLongitude ,checkProvider());

            }

            //當前選擇的位置判斷是否可運作
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {

                super.onLocationAvailability(locationAvailability);
                Log.v("ppking", "GPSonLocationAvailability ! " + locationAvailability.isLocationAvailable());
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

//        boolean gpsUsable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean gpsPresent = mPackageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
//
//        boolean networkUsable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        boolean networkPresent =
//                mPackageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);
//
//        if (gpsPresent && gpsUsable) {
//            Log.v("ppking", "GPS PROVIDER");
//            provider = "GPS";
//        } else if (networkPresent && networkUsable) {
//            Log.v("ppking", "NET PROVIDER");
//            provider = "NET";
//        } else {
//            Log.v("ppking", "NO PROVIDER!!");
//            provider = "None";
//        }
        provider = locationGpsRequest.getPriority()+"";
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
        locationGpsSettingsRequest = null;
        settingGpsRequestBuilder =null;
        locationGpsRequest = null;
        fusedLocationGpsProviderClient.removeLocationUpdates(locationGpsCallback);
        fusedLocationGpsProviderClient = null;
        settingsGpsClient = null;
        mPackageManager = null;
        mLocationManager = null;
        locationGpsCallback = null;
        mGpsLocation = null;
        if (lostGPSLocationTimer !=null){
            lostGPSLocationTimer.cancel();
            lostGPSLocationTimer = null;
        }
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
            if (getLocationCount >=10 && !isNoSignal){
                isNoSignal = true;
                clear();
                reset(PriorityDefine.PRIORITY_BALANCED_POWER_ACCURACY);
            }else if(isNoSignal){
                clear();
                reset(PriorityDefine.PRIORITY_HIGH_ACCURACY);
                isNoSignal = false;
            }
        }
    }

    public void initClient(){
        fusedLocationGpsProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        settingsGpsClient = LocationServices.getSettingsClient(mActivity);
        locationGpsRequest = new LocationRequest();
        settingGpsRequestBuilder = new LocationSettingsRequest.Builder();
    }

    private void reset(@PriorityDefine.PriorityType int type){
        initClient();
        createLocationRequest(mIntervalTime , mFastTime , type);
        startLocationUpdates();
    }

    private void clear(){
        locationGpsSettingsRequest = null;
        settingGpsRequestBuilder =null;
        locationGpsRequest = null;
        fusedLocationGpsProviderClient.removeLocationUpdates(locationGpsCallback);
        fusedLocationGpsProviderClient = null;
        settingsGpsClient = null;
        mPackageManager = null;
        mLocationManager = null;
        locationGpsCallback = null;
        mGpsLocation = null;
    }

}
