package com.example.shiningtechw.fusedlocationtest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.invocation.Location;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {


    private LogicTest logicTest;
    private LocationLogicTest locationLogicTest;

    @Before
    public void setUp(){
        logicTest = new LogicTest();
        locationLogicTest = new LocationLogicTest();
    }

    @Test
    public void changeProviderTest() throws Exception {

        logicTest.checkFusedLocationWork(102);

        assertEquals(101 , logicTest.getProvider());
    }

    @Test
    public void bothProviderNoWorkTest() throws Exception{
        int checkTime = 0;
        logicTest.checkFusedLocationWork(100);
        if (checkTime == 0){
            logicTest.countTime();
        }
        logicTest.checkFusedLocationWork(102);
        if (checkTime == 0){
            logicTest.countTime();
        }

        if (logicTest.getCountNet()==1 && logicTest.getCountGps()==1){
            locationLogicTest.startOriginalLocation(true);

        }
    }


    public class LocationLogicTest implements LocationLogic.OriginalLocationLogic{

        @Override
        public void startOriginalLocation(boolean isStart) {

        }

        @Override
        public boolean isGpsLocationWork() {
            return false;
        }

        @Override
        public boolean isNewLocationWork() {
            return false;
        }

        @Override
        public void changeProvider(boolean isChange) {

        }
    }


    public class LogicTest implements LocationLogic.FusedLocationLogic{
        private int provider;
        private int countGps;
        private int countNet;

        @Override
        public void checkFusedLocationWork(int provider) {
            if (provider==100){
                changeFusedLocation(true , provider);
            }else if (provider == 102){
                changeFusedLocation(true , provider);
            }
        }

        @Override
        public void changeFusedLocation(boolean isChange , int provider) {
            if (isChange){
                if (provider == 100){
                    provider = 102;
                    this.provider = provider;
                }else if (provider == 102){
                    provider = 100;
                    this.provider = provider;
                }
            }
        }
        private int getProvider(){
            return provider;
        }

        public void countTime(){
            if (provider == 100){
                countGps +=1;
            }else if (provider == 102){
                countNet +=1;
            }
        }

        public int getCountGps(){
            return countGps;
        }
        public int getCountNet(){
            return countNet;
        }
    }
}