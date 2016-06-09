package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class LTRLayouter implements ILayouter {

    private int canvasWidth;
    private int canvasHeight;

    private int viewRight;
    private int viewBottom;
    private int minTop;

    private List<Pair<Rect, View>> rowViews = new LinkedList<>();

    protected int currentViewWidth;
    protected int currentViewHeight;
    protected int currentViewBottom;

    public LTRLayouter(int canvasWidth, int canvasHeight, int rightOffset, int topOffset, int bottomOffset) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.viewBottom = bottomOffset;
        this.viewRight = rightOffset;
        this.minTop = topOffset;
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

    @Override
    public void calculateView(View view, RecyclerView.LayoutManager layoutManager) {
        currentViewHeight = layoutManager.getDecoratedMeasuredHeight(view);
        currentViewWidth = layoutManager.getDecoratedMeasuredWidth(view);

        currentViewBottom = layoutManager.getDecoratedBottom(view);
    }

    @Override
    public void layoutRow(SpanLayoutManager layoutManager) {
        int bufLeft = viewRight - currentViewWidth;
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        if (bufLeft < 0 && viewRight < getCanvasWidth()) {
            //if previously row finished and we have to fill it
            minTop = layoutManager.layoutRow(rowViews, minTop, viewBottom, viewRight, true);

            //clear row data
            rowViews.clear();

            //go to next row, increase top coordinate, reset left
            viewRight = getCanvasWidth();
            viewBottom = minTop;
        }
    }

    public void placeView(View view, RecyclerView.LayoutManager layoutManager) {

        /* view can be placed in current row, but we can't determine real position, until row will be filled,
        so generate rect for the view and layout it in the end of the row
         */

        int left = viewRight - currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(left, viewTop, viewRight, viewBottom);
        viewRight = left;

        rowViews.add(new Pair<>(viewRect, view));
    }

    @Override
    public void onAttachView(View view, RecyclerView.LayoutManager layoutManager) {
        viewRight = layoutManager.getDecoratedRight(view);
        minTop = Math.min(minTop, layoutManager.getDecoratedTop(view));
        viewBottom = minTop;
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewBottom < 0;
    }

}
