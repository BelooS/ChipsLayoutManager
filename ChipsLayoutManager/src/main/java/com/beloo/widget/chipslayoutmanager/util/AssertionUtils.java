package com.beloo.widget.chipslayoutmanager.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class AssertionUtils {
    private AssertionUtils() {}

    public static <T> void assertNotNull(@Nullable T object, @NonNull String parameterName) throws AssertionError {
        if (object == null)
            throw new AssertionError(parameterName + " can't be null.");
    }

    public static <T> void assertInstanceOf(@NonNull T object, @NonNull Class<?> clazz, @NonNull String parameterName) throws AssertionError {
        check(!clazz.isInstance(object), parameterName + " is not instance of " + clazz.getName() + ".");
    }

    public static <T> void assertNotEquals(@NonNull T object, @NonNull T anotherObject, @NonNull String parameterName) throws AssertionError {
        check(object == anotherObject || object.equals(anotherObject), parameterName + " can't be equal to " + String.valueOf(anotherObject) + ".");
    }

    public static void assertNotEmpty(String text, String parameterName) throws AssertionError {
        check(TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim()), parameterName + " can't be empty.");
    }

    public static void check(boolean b, @NonNull String message) {
        if (b)
            throw new AssertionError(message);
    }
}
