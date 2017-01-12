package com.beloo.widget.chipslayoutmanager.util.log;

import android.util.Log;

class AndroidLog implements LogWrapper {

    @Override
    public int d(String tag, String msg) {
        return Log.d(tag, msg);
    }

    @Override
    public int v(String tag, String msg) {
        return Log.v(tag, msg);
    }

    @Override
    public int w(String tag, String msg) {
        return Log.w(tag, msg);
    }

    @Override
    public int i(String tag, String msg) {
        return Log.i(tag, msg);
    }

    @Override
    public int e(String tag, String msg) {
        return Log.e(tag, msg);
    }
}
