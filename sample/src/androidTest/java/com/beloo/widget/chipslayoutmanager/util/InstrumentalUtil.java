package com.beloo.widget.chipslayoutmanager.util;

import java.util.concurrent.CountDownLatch;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class InstrumentalUtil {
    public static void waitForIdle() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        getInstrumentation().waitForIdle(countDownLatch::countDown);

        //assert
        countDownLatch.await();
    }
}
