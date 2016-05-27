package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class SpanLayoutManager extends RecyclerView.LayoutManager {

    private SparseArray<View> viewCache = new SparseArray<>();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        fill(recycler);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private void fill(RecyclerView.Recycler recycler) {

        View anchorView = getAnchorVisibleTopLeftView();
        viewCache.clear();

        //Помещаем вьюшки в кэш и...
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        //... и удалям из лэйаута
        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }

        fillUp(anchorView, recycler);
        fillDown(anchorView, recycler);

        //отправляем в корзину всё, что не потребовалось в этом цикле лэйаута
        //эти вьюшки или ушли за экран или не понадобились, потому что соответствующие элементы
        //удалились из адаптера
        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

    }

    private void fillUp(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos = 0;
        int anchorTop = 0;
        int anchorBottom = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorTop = getDecoratedTop(anchorView);
            anchorBottom = getDecoratedBottom(anchorView);
        }

        int viewLeft = 0;
        int maxTop = anchorBottom;

        int pos = anchorPos - 1;
        int viewBottom = anchorTop; //нижняя граница следующей вьюшки будет начитаться от верхней границы предыдущей
        boolean fillNext = viewBottom > 0;

        while (fillNext && pos >= 0) {
            View view = viewCache.get(pos); //проверяем кэш
            if (view == null) {
                view = recycler.getViewForPosition(pos);
                addView(view, 0);
                measureChildWithMargins(view, 0, 0);

                int viewHeight = getDecoratedMeasuredHeight(view);
                int viewWidth = getDecoratedMeasuredWidth(view);

                if (!(viewLeft == 0 || viewLeft + viewWidth <= getWidth())) {
                    //go to next row, increase top coordinate, reset left
                    viewLeft = 0;
                    viewBottom = maxTop;
                }

                fillNext = viewBottom >= 0;
                if (fillNext) {
                    //view can be placed in current row, layout it
                    layoutDecorated(view, viewLeft, viewBottom - viewHeight, viewLeft + viewWidth, viewBottom);
                    viewLeft = getDecoratedRight(view);
                    maxTop = Math.max(maxTop, getDecoratedTop(view));
                } else {
                    removeView(view);
                    recycler.recycleView(view);
                }

                pos--;

            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.

                int viewRight = getDecoratedRight(view);

                attachView(view);
                viewCache.remove(pos);

                maxTop = Math.max(maxTop, getDecoratedTop(view));
                viewBottom = maxTop;

                fillNext = viewBottom > 0 || viewRight < getWidth();

                viewLeft = 0;
                pos--;

            }
        }
    }


    private void fillDown(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos = 0;
        int anchorTop = 0;
        int anchorBottom = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorTop = getDecoratedTop(anchorView);
            anchorBottom = getDecoratedBottom(anchorView);
        }

        int pos = anchorPos;
        boolean fillNext = true;
        int height = getHeight();
        int viewTop = anchorTop;
        int viewLeft = 0;
        int maxBottom = anchorBottom;

        int itemCount = getItemCount();

        while (fillNext && pos < itemCount) {
            View view = viewCache.get(pos);
            if (view == null) {
                view = recycler.getViewForPosition(pos);
                //try to add view in current row
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int viewHeight = getDecoratedMeasuredHeight(view);
                int viewWidth = getDecoratedMeasuredWidth(view);

                if (!(viewLeft == 0 || viewLeft + viewWidth <= getWidth())) {
                    //go to next row, increase top coordinate, reset left
                    viewLeft = 0;
                    viewTop = maxBottom;
                }

                fillNext = viewTop <= height;

                if (fillNext) {
                    //view can be placed in current row, layout it
                    layoutDecorated(view, viewLeft, viewTop, viewLeft + viewWidth, viewTop + viewHeight);
                    viewLeft = getDecoratedRight(view);
                    maxBottom = Math.max(maxBottom, getDecoratedBottom(view));
                } else {
                    removeView(view);
                    recycler.recycleView(view);
                }

                pos++;

            } else {

                int viewRight = getDecoratedRight(view);

                attachView(view);
                viewCache.remove(pos);

                maxBottom = Math.max(maxBottom, getDecoratedBottom(view));
                viewTop = maxBottom;

                fillNext = viewTop <= height || viewRight < getWidth();

                viewLeft = 0;
                pos++;

            }

        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        fill(recycler);
        return dy;
    }

    private int scrollVerticallyInternal(int dy) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0) {
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
        if (dy < 0) {
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0) { //если верхняя вюшка не самая первая в адаптере
                delta = dy;
            } else { //если верхняя вьюшка самая первая в адаптере и выше вьюшек больше быть не может
                int viewTop = getDecoratedTop(firstView);
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0) { //если контент уезжает вверх
            View lastView = getChildAt(childCount - 1);
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1) { //если нижняя вюшка не самая последняя в адаптере
                delta = dy;
            } else { //если нижняя вьюшка самая последняя в адаптере и ниже вьюшек больше быть не может
                int viewBottom = getDecoratedBottom(lastView);
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
        }
        return delta;
    }

    /**
     * @return View, which is highest visible left view
     */
    private View getAnchorVisibleTopLeftView() {
        int childCount = getChildCount();
        View topLeft = null;

        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int top = getDecoratedTop(view);
            int bottom = getDecoratedBottom(view);
            int left = getDecoratedLeft(view);
            int right = getDecoratedRight(view);
            Rect viewRect = new Rect(left, top, right, bottom);
            boolean intersect = viewRect.intersect(mainRect);
            if (intersect) {
                topLeft = view;
                break;
            }
        }

        return topLeft;
    }

}
