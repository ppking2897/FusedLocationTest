package com.example.shiningtechw.fusedlocationtest;

import java.util.Timer;
import java.util.TimerTask;


public class LocationLogicImp implements LocationLogic.FusedLocationLogic {
    private int count;
    private Timer taskTimer;
    private int TIME = 1000;
    private int provider;

    public void checkFusedLocationWork(int provider) {
        //High accuracy
        if (provider == 100) {
            if (taskTimer != null) {
                taskTimer.cancel();
                taskTimer = null;
            }
            taskTimer = new Timer();
            taskTimer.schedule(new MyTask(), 0, TIME);
            getProvider(provider);
        }
    }

    @Override
    public void changeFusedLocation(boolean isChange , int provider) {
        if (isChange){

        }
    }

    public class MyTask extends TimerTask{
        @Override
        public void run() {
            //TODO 累加超過10秒轉換精確度
            count+=1;
            if (count >=10){
                changeFusedLocation(true , provider);
            }
        }
    }

    private void getProvider(int provider){
        this.provider = provider;
    }


}
