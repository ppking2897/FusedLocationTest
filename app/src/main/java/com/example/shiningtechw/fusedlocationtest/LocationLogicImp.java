package com.example.shiningtechw.fusedlocationtest;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ShiningTech.W on 2017/11/28.
 */

public class LocationLogicImp implements LocationLogic.FusedLocationLogic {
    private int count;
    private Timer taskTimer;
    private int TIME = 1000;


    @Override
    public void checkFusedLocationWork(int provider) {
        //High accuracy
        if (provider == 100) {
            if (taskTimer != null) {
                taskTimer.cancel();
                taskTimer = null;
            }
            taskTimer = new Timer();
            taskTimer.schedule(new MyTask(), 0, TIME);
        }
    }

    @Override
    public void changeFusedLocation(boolean isChange) {

        if (isChange){

        }

    }

    public class MyTask extends TimerTask{
        @Override
        public void run() {
            //TODO 累加超過10秒轉換精確度
            count+=1;
            if (count >=10){
                changeFusedLocation(true);
            }
        }
    }
}
