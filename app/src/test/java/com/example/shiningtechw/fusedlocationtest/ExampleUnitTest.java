package com.example.shiningtechw.fusedlocationtest;


import android.app.Activity;
import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Test
    public void test() throws Exception {
        // Context of the app under test.
        MainActivity activity = Mockito.mock(MainActivity.class);
        assertEquals(4 ,4);

    }
}