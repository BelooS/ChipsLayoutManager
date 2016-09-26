package com.example.beloo.spanlayoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.filters.RequiresDevice;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnitParamsRunner.class)
public class ExampleInstrumentationTest {
    @Test
    @MediumTest
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.beloo.spanlayoutmanager.test", appContext.getPackageName());
    }

    @Test
    @SmallTest
    @RequiresDevice
    @Parameters({"10", "9", "8", "7"})
    public void testRect(int width) {
        Rect r = new Rect(0,5,10,5);
        assertEquals(r.width(), width);
    }

    @Test(expected = IllegalArgumentException.class)
    @SmallTest
    public void testSomething() throws Exception {
        throw new IllegalArgumentException();
    }

}