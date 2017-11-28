package com.example.shiningtechw.fusedlocationtest;

import java.util.TimerTask;

/**
 * Created by ShiningTech.W on 2017/11/27.
 */

public interface LocationLogic {

    interface FusedLocationLogic{

        void checkFusedLocationWork(int provider);

        void changeFusedLocation(boolean isChange);


    }

    interface OriginalLocationLogic{

        void startOriginalLocation(boolean isStart);

        boolean isGpsLocationWork();

        boolean isNewLocationWork();

        void changeProvider(boolean isChange);
    }
}
