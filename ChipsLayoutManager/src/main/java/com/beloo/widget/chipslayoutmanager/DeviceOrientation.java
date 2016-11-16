package com.beloo.widget.chipslayoutmanager;

import android.content.res.Configuration;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({Configuration.ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_UNDEFINED})
@Retention(RetentionPolicy.SOURCE)
@interface DeviceOrientation {}
