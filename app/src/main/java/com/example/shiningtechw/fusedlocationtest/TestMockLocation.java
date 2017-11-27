package com.example.shiningtechw.fusedlocationtest;

import android.location.*;
import android.location.Location;

/**
 * Created by ShiningTech.W on 2017/11/27.
 */

public class TestMockLocation {
    private static final String PROVIDER = "flp";
    public TestMockLocation(){

    }

    public Location createLocation(double lat , double lng , float accuracy){
        android.location.Location newLocation = new android.location.Location(PROVIDER);
        newLocation.setLatitude(lat);
        newLocation.setLongitude(lng);
        newLocation.setAccuracy(accuracy);
        return newLocation;
    }
}
