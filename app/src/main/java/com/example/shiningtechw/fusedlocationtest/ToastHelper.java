package com.example.shiningtechw.fusedlocationtest;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ShiningTech.W on 2017/11/21.
 */

public class ToastHelper {
    private Context mContext;
    public ToastHelper(Context context){
        this.mContext = context;
    }
    public void showMyToast(String message){
        Toast.makeText(mContext , message , Toast.LENGTH_SHORT).show();
    }
}
