package com.beloo.widget.chipslayoutmanager.anchor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChildViewsIterable;
import com.beloo.widget.chipslayoutmanager.layouter.ICanvas;

public class RowsAnchorFactory extends AbstractAnchorFactory {

    private ChildViewsIterable childViews;

    public RowsAnchorFactory(RecyclerView.LayoutManager lm, ICanvas canvas) {
        super(lm, canvas);
        childViews = new ChildViewsIterable(lm);
    }

    /** get the highest views in layout. The closest to left border view will be picked from it. */
    @Override
    public AnchorViewState getAnchor() {

        AnchorViewState minPosView = AnchorViewState.getNotFoundState();

        int minPosition = Integer.MAX_VALUE;
        int minTop = Integer.MAX_VALUE;

        for (View view : childViews) {
            AnchorViewState anchorViewState = createAnchorState(view);
            int pos = lm.getPosition(view);
            int top = lm.getDecoratedTop(view);

            Rect viewRect = new Rect(anchorViewState.getAnchorViewRect());

            if (getCanvas().isInside(viewRect) && !anchorViewState.isRemoving()) {
                if (minPosition > pos) {
                    minPosition = pos;
                    minPosView = anchorViewState;
                }

                if (minTop > top) {
                    minTop = top;
                }
            }
        }

        if (!minPosView.isNotFoundState()) {
            minPosView.getAnchorViewRect().top = minTop;
            /* we don't need bottom coordinate for layouter
            also this helps to normalize row properly when anchor deleted and was the biggest view in a row
            */
            minPosView.setPosition(minPosition);
        }

        return minPosView;
    }

    @Override
    public void resetRowCoordinates(AnchorViewState anchorView) {
        if (!anchorView.isNotFoundState()) {
            Rect rect = anchorView.getAnchorViewRect();
            rect.left = getCanvas().getCanvasLeftBorder();
            rect.right = getCanvas().getCanvasRightBorder();
        }
    }

}
