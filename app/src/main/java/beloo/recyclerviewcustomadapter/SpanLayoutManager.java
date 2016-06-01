package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class SpanLayoutManager extends RecyclerView.LayoutManager {

    /** coefficient to support fast scrolling, caching views only for one row may not be enough */
    public static final float FAST_SCROLLING_COEFFICIENT = 2;
    private SparseArray<View> viewCache = new SparseArray<>();
    private int maxViewsInRow = 2;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        calcRecyclerCacheSize(recycler, 2);
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

//        if (viewCache.size() > 0) {
//            Log.d("cachedViews", "from " + viewCache.keyAt(0) + " to " + viewCache.keyAt(viewCache.size() - 1));
//        }

        fillUp(anchorView, recycler);
        fillDown(anchorView, recycler);

        //отправляем в корзину всё, что не потребовалось в этом цикле лэйаута
        //эти вьюшки или ушли за экран или не понадобились, потому что соответствующие элементы
        //удалились из адаптера
        int recycledSize = viewCache.size();
        for (int i = 0; i < viewCache.size(); i++) {
            removeAndRecycleView(viewCache.valueAt(i), recycler);
            Log.d("fill", "recycle position =" + viewCache.keyAt(i));
        }

        Log.d("fill", "recycled count = " + recycledSize);
    }

    private void fillUp(@Nullable View anchorView, RecyclerView.Recycler recycler) {
        int anchorPos = 0;
        int anchorTop = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
            anchorTop = getDecoratedTop(anchorView);
        }

        int viewRight = getWidth();
        int minTop = anchorTop;

        int pos = anchorPos - 1;
        int viewBottom = anchorTop; //нижняя граница следующей вьюшки будет начитаться от верхней границы предыдущей

        //in case we don't need fillup
        if (viewBottom < 0) return;

        List<Pair<Rect, View>> rowViews = new LinkedList<>();
        int rowViewsWidth = 0;

        int startCacheSize = viewCache.size();
        int requestedItems = 0;
        int recycledItems = 0;
        Log.d("fillUp", "cached items = " + startCacheSize);

        while (pos >= 0) {
            View view = viewCache.get(pos); //проверяем кэш
            if (view == null) {
//                Log.d("fillUp", "getView for position = " + pos);
                view = recycler.getViewForPosition(pos);
                requestedItems ++;

                measureChildWithMargins(view, 0, 0);

                int viewHeight = getDecoratedMeasuredHeight(view);
                int viewWidth = getDecoratedMeasuredWidth(view);

                if (rowViewsWidth + viewWidth > getWidth()) {
                    //if previously row finished and we have to fill it

                    layoutRow(rowViews, rowViewsWidth);

                    //clear row data
                    rowViewsWidth = 0;
                    rowViews.clear();

                    //go to next row, increase top coordinate, reset left
                    viewRight = getWidth();
                    viewBottom = minTop;
                }

                if (viewBottom < 0) {
                    /* reached end of visible bounds, exit.
                    recycle view, which was requested previously
                     */
                    recycler.recycleView(view);
                    recycledItems++;
                    break;
                }

                /* view can be placed in current row, but we can't determine real position, until row will be filled,
                so generate rect for the view and layout it in the end of the row
                 */

                int left = viewRight - viewWidth;
                int viewTop = viewBottom - viewHeight;
                Rect viewRect = new Rect(left, viewTop, viewRight, viewBottom);

                rowViews.add(new Pair<>(viewRect, view));

                viewRight = left;
                minTop = Math.min(minTop, getDecoratedTop(view));
                rowViewsWidth += viewWidth;

                pos--;

            } else {
                //если вьюшка есть в кэше - просто аттачим её обратно
                //нет необходимости проводить measure/layout цикл.

                viewRight = getDecoratedRight(view);

                //todo in case all views have same height
                int curViewBottom = getDecoratedBottom(view);
                //we reach invisible views, so stop drawing
                if (curViewBottom < 0) break;

                //fillup
                attachView(view);
                viewCache.remove(pos);

                minTop = Math.max(minTop, getDecoratedTop(view));
                viewBottom = minTop;

                pos--;
            }
        }

        Log.d("fillUp", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));

        //layout last row
        layoutRow(rowViews, rowViewsWidth);
    }

    private void layoutRow(List<Pair<Rect, View>> rowViews, int rowViewsWidth) {
        int offset = getWidth() - rowViewsWidth;

        for (int i = 0; i < rowViews.size(); i++) {
            Pair<Rect, View> rowViewRectPair = rowViews.get(i);
            Rect viewRect = rowViewRectPair.first;
            viewRect.left = viewRect.left - offset;
            viewRect.right = viewRect.right - offset;

            addView(rowViewRectPair.second, 0);
            //layout whole views in a row
            layoutDecorated(rowViewRectPair.second, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }
    }

    /** recycler should contain all recycled views from a longest row, not just 2 holders by default*/
    private void calcRecyclerCacheSize(RecyclerView.Recycler recycler, int rowSize) {
        maxViewsInRow = Math.max(rowSize, maxViewsInRow);
        recycler.setViewCacheSize((int) (maxViewsInRow * FAST_SCROLLING_COEFFICIENT));
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
        int rowSize = 0;

        int requestedItems = 0;
        int recycledItems = 0;
        int startCacheSize = viewCache.size();
        Log.d("fillDown", "cached items = " + startCacheSize);

        while (fillNext && pos < itemCount) {
            View view = viewCache.get(pos);
            if (view == null) {
//                Log.d("fillDown", "getView for position = " + pos);
                view = recycler.getViewForPosition(pos);
                requestedItems++;
                measureChildWithMargins(view, 0, 0);
                int viewHeight = getDecoratedMeasuredHeight(view);
                int viewWidth = getDecoratedMeasuredWidth(view);

                if (!(viewLeft == 0 || viewLeft + viewWidth <= getWidth())) {
                    //go to next row, increase top coordinate, reset left
                    viewLeft = 0;
                    viewTop = maxBottom;
                    fillNext = viewTop <= height;
                    calcRecyclerCacheSize(recycler, rowSize);
                    rowSize = 0;
                }

                if (fillNext) {
                    addView(view);
                    rowSize++;
                    //view can be placed in current row, layout it
                    layoutDecorated(view, viewLeft, viewTop, viewLeft + viewWidth, viewTop + viewHeight);
                    viewLeft = getDecoratedRight(view);
                    maxBottom = Math.max(maxBottom, getDecoratedBottom(view));
                } else {
                    recycledItems++;
                    recycler.recycleView(view);
                }

                pos++;

            } else {

                int cachedTop = getDecoratedTop(view);
                if (getHeight() < cachedTop) {
                    //we reached bottom of visible children
                    break;
                }

                int viewRight = getDecoratedRight(view);

                attachView(view);

                viewCache.remove(pos);

                maxBottom = Math.max(maxBottom, getDecoratedBottom(view));
                viewTop = maxBottom;

                fillNext = viewTop <= height || viewRight < getWidth();

                viewLeft = getDecoratedRight(view);
                pos++;

            }

        }

        Log.d("fillDown", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));
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

        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (getPosition(topView) == 0 && getPosition(bottomView) == getItemCount() - 1 && viewSpan <= getHeight()) {
            //where all objects fit on screen, no scrolling needed
            return 0;
        }

        int delta = 0;
        //if content scrolled down
        if (dy < 0) {
            View firstView = getChildAt(0);
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0) { //если верхняя вюшка не самая первая в адаптере
                delta = dy;
            } else { //если верхняя вьюшка самая первая в адаптере и выше вьюшек больше быть не может
                int viewTop = getDecoratedTop(firstView);
                delta = Math.max(viewTop, dy);
            }
        } else if (dy > 0) { //if content scrolled up
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
