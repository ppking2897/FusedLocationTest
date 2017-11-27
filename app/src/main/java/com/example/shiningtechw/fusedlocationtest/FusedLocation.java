package com.example.shiningtechw.fusedlocationtest;

import android.app.Activity;

/**
 * Created by ShiningTech.W on 2017/11/21.
 */

public interface FusedLocation {

    void createLocationRequest(long intervalTime , long fastTime , @PriorityDefine.PriorityType int type);
    void startLocationUpdates();
    void createLocationCallback();
    String checkProvider();

    void setFusedCallback(FusedCallback callback);

    void destroy();

    interface FusedCallback{
        void getLocation(double latitude , double longitude , String provider);
        void getChangeAccuracyMessage(float speed);
    }
}
