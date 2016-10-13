package com.beloo.widget.spanlayoutmanager.cache;

public class ViewCacheFactory {
    public IViewCacheStorage createCacheStorage() {
        return new ViewCacheStorage();
    }
}
