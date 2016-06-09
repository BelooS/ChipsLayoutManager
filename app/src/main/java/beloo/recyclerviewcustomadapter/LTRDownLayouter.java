package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

class LTRDownLayouter extends AbstractLayouter {

    private int maxBottom;
    private int viewLeft;

    public LTRDownLayouter(int canvasHeight, int canvasWidth, int leftOffset, int topOffset, int bottomOffset) {
        super(canvasHeight, canvasWidth, topOffset, bottomOffset);
        viewLeft = leftOffset;
    }

    @Override
    public void layoutRow(SpanLayoutManager layoutManager) {
        super.layoutRow(layoutManager);
        //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)

        //layout previously calculated row
        layoutManager.layoutRow(rowViews, viewTop, maxBottom, 0, false);

        //go to next row, increase top coordinate, reset left
        viewLeft = 0;
        viewTop = maxBottom;

        //clear row data
        rowViews.clear();
    }

    @Override
    public boolean shouldLayoutRow() {
        return viewLeft > 0 && viewLeft + currentViewWidth > getCanvasWidth();
    }

    @Override
    public void placeView(View view, RecyclerView.LayoutManager layoutManager) {
        Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + currentViewWidth, viewTop + currentViewHeight);
        rowViews.add(new Pair<>(viewRect, view));

        viewLeft = viewRect.right;
        maxBottom = Math.max(maxBottom, viewRect.bottom);
    }

    @Override
    public void onAttachView(View view, RecyclerView.LayoutManager layoutManager) {
        super.onAttachView(view, layoutManager);
        maxBottom = Math.max(maxBottom, layoutManager.getDecoratedBottom(view));

        viewLeft = layoutManager.getDecoratedRight(view);

        if (!(viewLeft == 0 || viewLeft + layoutManager.getDecoratedMeasuredWidth(view) <= getCanvasWidth())) {
            //new row in cached views
            viewTop = maxBottom;
        }
    }

    @Override
    public boolean isFinishedLayouting() {
        return viewTop > getCanvasHeight();
    }

}
