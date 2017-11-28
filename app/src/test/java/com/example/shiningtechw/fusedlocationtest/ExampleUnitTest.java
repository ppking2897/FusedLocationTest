package com.example.shiningtechw.fusedlocationtest;


import android.app.Activity;
import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {



    @Test
    public void test() throws Exception {
       LocationLogicImp locationLogicImp = Mockito.mock(LocationLogicImp.class);

       locationLogicImp.checkFusedLocationWork(100);


    }
}