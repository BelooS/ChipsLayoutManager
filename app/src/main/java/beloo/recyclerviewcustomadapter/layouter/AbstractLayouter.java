package beloo.recyclerviewcustomadapter.layouter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import beloo.recyclerviewcustomadapter.SpanLayoutManager;

abstract class AbstractLayouter implements ILayouter {
    protected int currentViewWidth;
    protected int currentViewHeight;
    protected int currentViewBottom;
    protected List<Pair<Rect, View>> rowViews = new LinkedList<>();
    protected int viewBottom;
    protected int viewTop;
    protected int rowSize = 0;
    protected int previousRowSize;

    protected SpanLayoutManager layoutManager;

    AbstractLayouter(SpanLayoutManager layoutManager, int topOffset, int bottomOffset) {
        this.layoutManager = layoutManager;
        this.viewTop = topOffset;
        this.viewBottom = bottomOffset;
    }

    int getCanvasWidth() {
        return layoutManager.getWidth();
    }

    int getCanvasHeight() {
        return layoutManager.getHeight();
    }

    @Override
    public void calculateView(View view) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);

        currentViewBottom = layoutManager.getDecoratedBottom(view);
    }

    @Override
    public int getPreviousRowSize() {
        return previousRowSize;
    }

    @CallSuper
    @Override
    public void onAttachView(View view) {
        rowSize++;
    }

    @CallSuper
    @Override
    public void layoutRow() {
        previousRowSize = rowSize;
        this.rowSize = 0;
    }
}
