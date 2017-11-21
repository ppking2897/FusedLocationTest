package com.example.shiningtechw.fusedlocationtest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by ShiningTech.W on 2017/11/21.
 *
 * 與Activity內的onRequestPermissionsResult取得結果做動作
 *
 */

public class PermissionCheckImp implements PermissionCheck {
    public Activity mActivity;

    public PermissionCheckImp(Activity activity){
        this.mActivity = activity;
    }

    @Override
    public boolean checkPermissions(String[] permissions) {

        int permissionState = ActivityCompat.checkSelfPermission(mActivity,
                permissions[0]);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermissions(String[] permissions , int REQUEST_PERMISSIONS_REQUEST_CODE ) {
        ActivityCompat.requestPermissions(mActivity,
                permissions,
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void resultCallback(boolean isSuccess) {
        if (!isSuccess){
            ToastHelper mToastHelper = new ToastHelper(mActivity);
            mToastHelper.showMyToast("請允許權限,否則無法定位");
        }
    }
}
