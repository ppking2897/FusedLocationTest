package com.example.shiningtechw.fusedlocationtest;

import com.google.android.gms.location.LocationSettingsRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Created by ShiningTech.W on 2017/11/29.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogicTest {
    @Test
    public void test(){
        LocationLogicImp locationLogicImp = Mockito.mock(LocationLogicImp.class);

        locationLogicImp.checkFusedLocationWork(100);

        Mockito.verify(locationLogicImp).checkFusedLocationWork(100);
    }
}
