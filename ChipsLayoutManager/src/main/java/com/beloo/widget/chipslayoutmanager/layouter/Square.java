package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChildViewsIterable;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

abstract class Square implements ICanvas {

    RecyclerView.LayoutManager lm;
    private ChildViewsIterable childViews;

    /**
     * highest view in layout. Have always actual value, because it set in {@link ChipsLayoutManager#onLayoutChildren}
     */
    private View topView;
    /**
     * lowest view in layout. Have always actual value, because it set in {@link ChipsLayoutManager#onLayoutChildren}
     */
    private View bottomView;

    /**
     * The view have placed in the closest to the left border. Have always actual value, because it set in {@link ChipsLayoutManager#onLayoutChildren}
     */
    private View leftView;

    /** The view have placed in the closest to the right border. Have always actual value, because it set in {@link ChipsLayoutManager#onLayoutChildren} */
    private View rightView;

    /** minimal position visible on screen*/
    private Integer minPositionOnScreen;
    private Integer maxPositionOnScreen;

    private boolean isFirstItemAdded;

    Square(RecyclerView.LayoutManager lm) {
        this.lm = lm;
        childViews = new ChildViewsIterable(lm);
    }

    @Override
    public Rect getCanvasRect() {
        return new Rect(getCanvasLeftBorder(), getCanvasTopBorder(), getCanvasRightBorder(), getCanvasBottomBorder());
    }

    @Override
    public Rect getViewRect(View view) {
        int left = lm.getDecoratedLeft(view);
        int top = lm.getDecoratedTop(view);
        int right = lm.getDecoratedRight(view);
        int bottom = lm.getDecoratedBottom(view);
        return new Rect(left, top, right, bottom);
    }

    @Override
    public boolean isInside(Rect rectCandidate) {
        //intersection changes rect!!!
        Rect intersect = new Rect(rectCandidate);
        return getCanvasRect().intersect(intersect);
    }

    @Override
    public boolean isInside(View viewCandidate) {
        return isInside(getViewRect(viewCandidate));
    }

    @Override
    public boolean isFullyVisible(View view) {
        Rect rect = getViewRect(view);
        return isFullyVisible(rect);
    }

    @Override
    public boolean isFullyVisible(Rect rect) {
        return rect.top >= getCanvasTopBorder()
                && rect.bottom <= getCanvasBottomBorder()
                && rect.left >= getCanvasLeftBorder()
                && rect.right <= getCanvasRightBorder();
    }

    /**
     * find highest & lowest views among visible attached views
     */
    @Override
    public void findBorderViews() {
        topView = null;
        bottomView = null;
        leftView = null;
        rightView = null;
        minPositionOnScreen = RecyclerView.NO_POSITION;
        maxPositionOnScreen = RecyclerView.NO_POSITION;

        isFirstItemAdded = false;

        if (lm.getChildCount() > 0) {
            View initView = lm.getChildAt(0);
            topView = initView;
            bottomView = initView;
            leftView = initView;
            rightView = initView;

            for (View view : childViews) {
                int position = lm.getPosition(view);

                if (!isInside(view)) continue;

                if (lm.getDecoratedTop(view) < lm.getDecoratedTop(topView)) {
                    topView = view;
                }

                if (lm.getDecoratedBottom(view) > lm.getDecoratedBottom(bottomView)) {
                    bottomView = view;
                }

                if (lm.getDecoratedLeft(view) < lm.getDecoratedLeft(leftView)) {
                    leftView = view;
                }

                if (lm.getDecoratedRight(view) > lm.getDecoratedRight(rightView)) {
                    rightView = view;
                }

                if (minPositionOnScreen == RecyclerView.NO_POSITION || position < minPositionOnScreen) {
                    minPositionOnScreen = position;
                }

                if (maxPositionOnScreen == RecyclerView.NO_POSITION || position > maxPositionOnScreen) {
                    maxPositionOnScreen = position;
                }

                if (position == 0) {
                    isFirstItemAdded = true;
                }
            }
        }
    }

    @Override
    public View getTopView() {
        return topView;
    }

    @Override
    public View getBottomView() {
        return bottomView;
    }

    @Override
    public View getLeftView() {
        return leftView;
    }

    @Override
    public View getRightView() {
        return rightView;
    }

    @Override
    public Integer getMinPositionOnScreen() {
        return minPositionOnScreen;
    }

    @Override
    public Integer getMaxPositionOnScreen() {
        return maxPositionOnScreen;
    }

    @Override
    public boolean isFirstItemAdded() {
        return isFirstItemAdded;
    }
}
