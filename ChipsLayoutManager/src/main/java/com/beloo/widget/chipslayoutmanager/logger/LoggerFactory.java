package com.beloo.widget.chipslayoutmanager.logger;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

public class LoggerFactory {
    @NonNull
    public IFillLogger getFillLogger(SparseArray<View> viewCache) {
        return new FillLogger(viewCache);
    }

    @NonNull
    public IAdapterActionsLogger getAdapterActionsLogger() {
        return new EmptyAdapterActionsLogger();
    }

    @NonNull
    public IPredictiveAnimationsLogger getPredictiveAnimationsLogger() {
        return new PredictiveAnimationsLogger();
    }

    @NonNull
    public IScrollingLogger getScrollingLogger() {
        return new EmtpyScrollingLogger();
    }
}
