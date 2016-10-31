package com.beloo.widget.chipslayoutmanager.logger;

import android.support.annotation.NonNull;

public class LoggerFactory {
    @NonNull
    public IFillLogger getFillLogger() {
        return new EmptyFillLogger();
    }

    @NonNull
    public IAdapterActionsLogger getAdapterActionsLogger() {
        return new AdapterActionsLogger();
    }
}
