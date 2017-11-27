package com.example.shiningtechw.fusedlocationtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ShiningTech.W on 2017/11/23.
 */

public class MyLocation {
    private Activity mActivity;
    private String locationProvider;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public MyLocation(MainActivity activity){
        this.mActivity = activity;
        locationManager = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
        locationProvider = LocationManager.NETWORK_PROVIDER;
        locationListener = new LocationListener();
    }

    @SuppressLint("MissingPermission")
    public void locationTest(){
        android.location.Location location = locationManager.getLastKnownLocation(locationProvider);

        locationManager.requestLocationUpdates(locationProvider, 10000, 0, locationListener);
    }

    public class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(android.location.Location location) {
            Log.v("ppking" , " MyLocation getLatitude " + location.getLatitude());
            Log.v("ppking" , " MyLocation getLongitude " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    public void removeListener(){
        locationManager.removeUpdates(locationListener);
    }

}
