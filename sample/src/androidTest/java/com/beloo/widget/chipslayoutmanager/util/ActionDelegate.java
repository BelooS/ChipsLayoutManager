package com.beloo.widget.chipslayoutmanager.util;


import android.support.test.espresso.UiController;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.support.BiConsumer;

public class ActionDelegate<T extends View> extends Action<T> {

    private BiConsumer<UiController, T> viewConsumer;

    public ActionDelegate(BiConsumer<UiController, T> viewConsumer) {
        this.viewConsumer = viewConsumer;
    }

    @Override
    public final void performAction(UiController uiController, T view) {
        viewConsumer.accept(uiController, view);
    }
}
