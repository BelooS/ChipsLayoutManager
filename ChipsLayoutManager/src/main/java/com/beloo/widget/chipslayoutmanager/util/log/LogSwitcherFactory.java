package com.beloo.widget.chipslayoutmanager.util.log;

public class LogSwitcherFactory {
    public static final int ADAPTER_ACTIONS = 1;
    public static final int ANCHOR_SCROLLING = 2;
    public static final int FILL = 3;
    public static final int PREDICTIVE_ANIMATIONS = 4;
    public static final int SCROLLING = 5;
    public static final int START_POSITION_LOGGER = 6;

    Log.LogSwitcher logSwitcher(){
        return new Log.LogSwitcher();
    }
}
