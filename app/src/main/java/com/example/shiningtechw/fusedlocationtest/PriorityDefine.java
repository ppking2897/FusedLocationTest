package com.example.shiningtechw.fusedlocationtest;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ShiningTech.W on 2017/11/20.
 */

public class PriorityDefine {
    public static final int PRIORITY_HIGH_ACCURACY = 100;
    public static final int PRIORITY_BALANCED_POWER_ACCURACY = 102;
    public static final int PRIORITY_LOW_POWER = 104;


    @IntDef({PRIORITY_HIGH_ACCURACY , PRIORITY_BALANCED_POWER_ACCURACY , PRIORITY_LOW_POWER})
    @Retention(RetentionPolicy.SOURCE)

    public @interface PriorityType{

    }

    @PriorityType int type = getType();

    @PriorityType
    public int getType(){
        switch (type){
            case PRIORITY_HIGH_ACCURACY:
                break;
            case PRIORITY_BALANCED_POWER_ACCURACY:
                break;
            case PRIORITY_LOW_POWER:
                break;
        }return type;
    }
}
