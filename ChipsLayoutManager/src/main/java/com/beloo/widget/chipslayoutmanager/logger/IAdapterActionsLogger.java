package com.beloo.widget.chipslayoutmanager.logger;

public interface IAdapterActionsLogger {
    void onItemsAdded(int positionStart, int itemCount);
    void onItemsRemoved(int positionStart, int itemCount);
    void onItemsChanged();
    void onItemsUpdated(int positionStart, int itemCount);
    void onItemsMoved(int from, int to, int itemCount);
}
