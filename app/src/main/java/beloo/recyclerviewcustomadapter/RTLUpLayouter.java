package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

class RTLUpLayouter extends AbstractLayouter implements ILayouter {

    protected int viewLeft;

    public RTLUpLayouter(int canvasWidth, int canvasHeight, int leftOffset, int topOffset, int bottomOffset) {
        super(canvasHeight, canvasWidth, topOffset, bottomOffset);
        this.viewLeft = leftOffset;
    }

    @Override
    public void layoutRow(SpanLayoutManager layoutManager) {
        super.layoutRow(layoutManager);

        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
        //if previously row finished and we have to fill it
        viewTop = layoutManager.layoutRow(rowViews, viewTop, viewBottom, -(getCanvasWidth() - viewLeft), true);

        //clear row data
        rowViews.clear();

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewBottom = viewTop;
    }

    public void placeView(View view, RecyclerView.LayoutManager layoutManager) {

        /* view can be placed in current row, but we can't determine real position, until row will be filled,
        so generate rect for the view and layout it in the end of the row
         */

        int right = viewLeft + currentViewWidth;
        int viewTop = viewBottom - currentViewHeight;
        Rect viewRect = new Rect(viewLeft, viewTop, right, viewBottom);
        viewLeft = right;

        rowViews.add(new Pair<>(viewRect, view));
    }

    @Override
    public void onAttachView(View view, RecyclerView.LayoutManager layoutManager) {
        super.onAttachView(view, layoutManager);
        viewLeft = 0;
        viewTop = Math.min(viewTop, layoutManager.getDecoratedTop(view));
        viewBottom = viewTop;
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewBottom < 0;
    }

    @Override
    public boolean shouldLayoutRow() {
        int bufRight = viewLeft + currentViewWidth;
        return bufRight > getCanvasWidth() && viewLeft > 0;
    }

}
