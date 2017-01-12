package com.beloo.widget.chipslayoutmanager.util.log;

class SilentLog implements LogWrapper {
    @Override
    public int d(String tag, String msg) {
        //no op
        return 0;
    }

    @Override
    public int v(String tag, String msg) {
        //no op
        return 0;
    }

    @Override
    public int w(String tag, String msg) {
        //no op
        return 0;
    }

    @Override
    public int i(String tag, String msg) {
        //no op
        return 0;
    }

    @Override
    public int e(String tag, String msg) {
        //no op
        return 0;
    }
}
