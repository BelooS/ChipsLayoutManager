package com.beloo.widget.chipslayoutmanager.util;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class InstrumentalUtil {
    public static void waitForIdle() throws Exception {
        getInstrumentation().waitForIdleSync();
    }
}
