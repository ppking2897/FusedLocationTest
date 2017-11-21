package com.example.shiningtechw.fusedlocationtest;

import android.app.Activity;

/**
 * Created by ShiningTech.W on 2017/11/21.
 */

public interface PermissionCheck {
    boolean checkPermissions(String[] requestPermissions);

    void requestPermissions(String[] requestPermissions , int REQUEST_PERMISSIONS_REQUEST_CODE);

    void resultCallback(boolean isSuccess);
}
