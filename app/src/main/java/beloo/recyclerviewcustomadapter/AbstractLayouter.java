package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractLayouter implements ILayouter {
    protected int canvasWidth;
    protected int canvasHeight;
    protected int currentViewWidth;
    protected int currentViewHeight;
    protected int currentViewBottom;
    protected List<Pair<Rect, View>> rowViews = new LinkedList<>();
    protected int viewRight;
    protected int viewBottom;
    protected int viewTop;

    public AbstractLayouter(int canvasHeight, int canvasWidth, int rightOffset, int topOffset, int bottomOffset) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        this.viewRight = rightOffset;
        this.viewTop = topOffset;
        this.viewBottom = bottomOffset;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void calculateView(View view, RecyclerView.LayoutManager layoutManager) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);

        currentViewBottom = layoutManager.getDecoratedBottom(view);
    }
}
