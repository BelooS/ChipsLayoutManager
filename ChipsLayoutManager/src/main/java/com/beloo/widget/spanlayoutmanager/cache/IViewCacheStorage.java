package com.beloo.widget.spanlayoutmanager.cache;

import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public interface IViewCacheStorage {

    /** @return rect of view position stored in cache. Could be null
     * @param position position of view, which rect could stored in cache */
    @Nullable
    Rect getRect(int position);

    void put(Rect rect, int position);

    /** purge whole cache*/
    void purge();

    /** all cache to selected position will be purged
     * @param position the end position, excluded */
    void purgeCacheToPosition(int position);

    /** all cache from selected position will be purged
     * @param position the start position, included */
    void purgeCacheFromPosition(int position);

    /** persist cache storage content to {@link Parcelable}*/
    Parcelable persist();
}
