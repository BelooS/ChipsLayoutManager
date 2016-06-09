package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

class RTLDownLayouter extends AbstractLayouter {

    private int maxBottom;
    private int viewRight;

    public RTLDownLayouter(int canvasHeight, int canvasWidth, int rightOffset, int topOffset, int bottomOffset) {
        super(canvasHeight, canvasWidth, topOffset, bottomOffset);
        viewRight = rightOffset;
    }

    @Override
    public void layoutRow(SpanLayoutManager layoutManager) {
        super.layoutRow(layoutManager);
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutManager.layoutRow(rowViews, viewTop, maxBottom, 0, false);

        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasWidth();
        viewTop = maxBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    public boolean shouldLayoutRow() {
        return viewRight < getCanvasWidth() && viewRight - currentViewWidth < 0;
    }

    @Override
    public void placeView(View view, RecyclerView.LayoutManager layoutManager) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, viewTop, viewRight, viewTop + currentViewHeight);
        rowViews.add(new Pair<>(viewRect, view));

        viewRight = viewRect.left;
        maxBottom = Math.max(maxBottom, viewRect.bottom);
    }

    @Override
    public void onAttachView(View view, RecyclerView.LayoutManager layoutManager) {
        super.onAttachView(view, layoutManager);
        maxBottom = Math.max(maxBottom, layoutManager.getDecoratedBottom(view));

        viewRight = layoutManager.getDecoratedLeft(view);

        if (!(viewRight == getCanvasWidth() || viewRight - layoutManager.getDecoratedMeasuredWidth(view) >= 0)) {
            //new row in cached views
            viewTop = maxBottom;
        }
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewTop > getCanvasHeight();
    }

}
