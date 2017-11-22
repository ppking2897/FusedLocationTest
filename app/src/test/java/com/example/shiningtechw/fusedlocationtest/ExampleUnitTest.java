package com.example.shiningtechw.fusedlocationtest;

import android.Manifest;
import android.content.pm.PackageInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.robolectric.shadows.ShadowSystemProperties.getBoolean;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ExampleUnitTest {

    private MainActivity mainActivity;

    @Mock
    private FusedLocationImp fusedLocationImp;

    @Before
    public void setUp(){
        mainActivity = Robolectric.setupActivity(MainActivity.class);


    }

    @Test
    public void addition_isCorrect() throws Exception {
        when(fusedLocationImp.checkProvider()).thenReturn("GPS");

        String provider = fusedLocationImp.checkProvider();

        assertEquals("GPS" , provider);
    }
}