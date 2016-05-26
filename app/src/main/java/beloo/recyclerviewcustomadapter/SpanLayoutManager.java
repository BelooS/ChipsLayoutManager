package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;

public class SpanLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        fillDown(recycler);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private void fillDown(RecyclerView.Recycler recycler) {
        int pos = 0;
        boolean fillDown = true;
        int height = getHeight();
        int viewTop = 0;
        int viewLeft = 0;
        int maxBottom = 0;

        int itemCount = getItemCount();

        while (fillDown && pos < itemCount){

            View view = recycler.getViewForPosition(pos);
            //try to add view in current row
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int viewHeight = getDecoratedMeasuredHeight(view);
            int viewWidth = getDecoratedMeasuredWidth(view);

            if (viewLeft == 0 || viewLeft + viewWidth <= getWidth()) {
                //view can be placed in current row, layout it
                layoutDecorated(view, viewLeft, viewTop, viewLeft + viewWidth, viewTop + viewHeight);
                viewLeft = getDecoratedRight(view);
                maxBottom = Math.max(maxBottom, getDecoratedBottom(view));
            } else {
                //view can't be placed in current row, remove
                removeView(view);
                //go to next row, increase top coordinate, reset left
                viewLeft = 0;
                viewTop = maxBottom;
                fillDown = viewTop <= height;
                continue;
            }

            pos++;
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        return dy;
    }

    private int scrollVerticallyInternal(int dy) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0){
            return 0;
        }

        final View topView = getChildAt(0);
        final View bottomView = getChildAt(childCount - 1);

        //Случай, когда все вьюшки поместились на экране
        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (viewSpan <= getHeight()) {
            return 0;
        }

        int delta = 0;
        //если контент уезжает вниз
        if (dy < 0){
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0){ //если верхняя вюшка не самая первая в адаптере
                delta = dy;
            } else { //если верхняя вьюшка самая первая в адаптере и выше вьюшек больше быть не может
                int viewTop = getDecoratedTop(firstView);
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0){ //если контент уезжает вверх
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1){ //если нижняя вюшка не самая последняя в адаптере
                delta = dy;
            } else { //если нижняя вьюшка самая последняя в адаптере и ниже вьюшек больше быть не может
                int viewBottom = getDecoratedBottom(lastView);
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
        }
        return delta;
    }

    //метод вернет вьюшку с максимальной видимой площадью
    private View getAnchorView() {
        int childCount = getChildCount();
        HashMap<Integer, View> viewsOnScreen = new HashMap<>();
        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            Rect viewRect = new Rect(left, top, right, bottom);
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect){
                int square = viewRect.width() * viewRect.height();
                viewsOnScreen.put(square, view);
            }
        }
        if (viewsOnScreen.isEmpty()){
            return null;
        }
        Integer maxSquare = null;
        for (Integer square : viewsOnScreen.keySet()) {
            if (maxSquare == null){
                maxSquare = square;
            } else {
                maxSquare = Math.max(maxSquare, square);
            }
        }
        return viewsOnScreen.get(maxSquare);
    }

}
