package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import beloo.recyclerviewcustomadapter.gravityModifier.GravityModifiersFactory;
import beloo.recyclerviewcustomadapter.gravityModifier.IGravityModifier;

public class SpanLayoutManager extends RecyclerView.LayoutManager {

    private IChildGravityResolver childGravityResolver = new CenterChildGravity();
    private GravityModifiersFactory gravityModifiersFactory = new GravityModifiersFactory();

    /** coefficient to support fast scrolling, caching views only for one row may not be enough */
    public static final float FAST_SCROLLING_COEFFICIENT = 2;
    private int maxViewsInRow = 2;


    private SparseArray<View> viewCache = new SparseArray<>();

    private Integer anchorViewPosition = null;

    /** highest top position of attached views*/
    private int highestViewTop = Integer.MAX_VALUE;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter,
                                 RecyclerView.Adapter newAdapter) {
        //Completely scrap the existing layout
        removeAllViews();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //We have nothing to show for an empty data set but clear any existing views
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        View anchorView = getAnchorVisibleTopLeftView();
        if (!state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);
            calcRecyclerCacheSize(recycler, 2);

            if (anchorView != null && anchorViewPosition != null && anchorViewPosition == 0) {
                //we can't add view in a hidden area if added view inserted on a zero position. so needed workaround here, we reset anchor position to 0
                fill(recycler, anchorView, anchorViewPosition);
                anchorViewPosition = null;
            } else {
                fill(recycler, anchorView);
            }
        } else {
            if (anchorView != null)
                anchorViewPosition = getPosition(anchorView);

        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    private void fill(RecyclerView.Recycler recycler, @Nullable View anchorView) {
        int anchorPos = 0;
        if (anchorView != null) {
            anchorPos = getPosition(anchorView);
        }

        fill(recycler, anchorView, anchorPos);
    }

    private void fill(RecyclerView.Recycler recycler, @Nullable View anchorView, int startingPos) {

        int anchorTop = 0;
        int anchorBottom = 0;
        int anchorLeft = 0;
        int anchorRight = 0;
        if (anchorView != null) {
            anchorTop = getDecoratedTop(anchorView);
            anchorBottom = getDecoratedBottom(anchorView);
            anchorLeft = getDecoratedLeft(anchorView);
            anchorRight = getDecoratedRight(anchorView);
        }

        highestViewTop = Integer.MAX_VALUE;
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


        //we should include anchor view here, so anchorLeft is a leftOffset
        fillDown(recycler, anchorTop, anchorBottom, anchorLeft, startingPos);
        //we shouldn't include anchor view here, so anchorLeft is a rightOffset
        fillUp(recycler, Math.min(anchorTop, highestViewTop), anchorLeft, anchorBottom, startingPos - 1);

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

    protected boolean isLayoutRTL() {
        return getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    /**
     * @param rightOffset left border of anchor view. Needed to try fill row to left of it.
     * */
    private void fillUp(RecyclerView.Recycler recycler, int topOffset, int rightOffset, int bottomOffset, int startingPos) {
        int viewRight = rightOffset;
        int viewLeft = 0;
        int minTop = topOffset;

        int pos = startingPos;

        int viewBottom = bottomOffset; //нижняя граница следующей вьюшки будет начитаться от верхней границы предыдущей

        List<Pair<Rect, View>> rowViews = new LinkedList<>();

        int startCacheSize = viewCache.size();
        int requestedItems = 0;
        int recycledItems = 0;
        Log.d("fillUp", "cached items = " + startCacheSize);

        while (pos >= 0) {
            View view = viewCache.get(pos); //проверяем кэш
            if (view == null) {
                Log.i("fillUp", "getView for position = " + pos);
                view = recycler.getViewForPosition(pos);
                requestedItems ++;

                measureChildWithMargins(view, 0, 0);

                int viewHeight = getDecoratedMeasuredHeight(view);
                int viewWidth = getDecoratedMeasuredWidth(view);

                int bufLeft = viewRight - viewWidth;

                //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
                if (bufLeft < 0 && viewRight < getWidth()) {
                    //if previously row finished and we have to fill it
                    minTop = layoutRow(rowViews, minTop, viewBottom, viewLeft, true);

                    //clear row data
                    rowViews.clear();

                    //go to next row, increase top coordinate, reset left
                    viewRight = getWidth();
                    viewBottom = minTop;
//                    Log.i("layout row", "new bottom = " + viewBottom);
//                    Log.i("layout row", "next position = " + pos);
                }

                if (viewBottom < 0) {
//                    Log.w("fill up", "reached end of visible bounds");
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
                viewLeft = left;

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

                minTop = Math.min(minTop, getDecoratedTop(view));
                viewBottom = minTop;

                pos--;
            }
        }

        Log.d("fillUp", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));

        //layout last row
        layoutRow(rowViews, minTop, viewBottom, viewLeft, true);
    }

    /** layout pre-calculated row on a recyclerView canvas
     * @param isReverseOrder if fill views from the end this flag have to be true to not break child position in recyclerView
     * returns minTop */
    private int layoutRow(List<Pair<Rect, View>> rowViews, int minTop, int maxBottom, int leftOffsetOfRow, boolean isReverseOrder) {
        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            viewRect.left = viewRect.left - leftOffsetOfRow;
            viewRect.right = viewRect.right - leftOffsetOfRow;

            minTop = Math.min(minTop, viewRect.top);
            maxBottom = Math.max(maxBottom, viewRect.bottom);
        }

        for (Pair<Rect, View> rowViewRectPair : rowViews) {
            Rect viewRect = rowViewRectPair.first;
            View view = rowViewRectPair.second;

            @SpanLayoutChildGravity
            int viewGravity = childGravityResolver.getItemGravity(getPosition(view));
            IGravityModifier gravityModifier = gravityModifiersFactory.getGravityModifier(viewGravity);
            gravityModifier.modifyChildRect(minTop, maxBottom, viewRect);

            if (isReverseOrder) {
                addView(view, 0);
            } else {
                addView(view);
            }

            //layout whole views in a row
            layoutDecorated(view, viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        }

        return minTop;
    }

    private void fillDown(RecyclerView.Recycler recycler, int topOffset, int bottomOffset, int leftOffset, int startingPos) {

        int pos = startingPos;
        int viewTop = topOffset;
        int viewLeft = 0;
        int maxBottom = bottomOffset;

        int itemCount = getItemCount();
        int rowSize = 0;

        int requestedItems = 0;
        int recycledItems = 0;
        int startCacheSize = viewCache.size();
        Log.d("fillDown", "cached items = " + startCacheSize);

        List<Pair<Rect, View>> rowViews = new LinkedList<>();

        while (pos < itemCount) {
            View view = viewCache.get(pos);
            if (view == null) {
                Log.i("fillDown", "getView for position = " + pos);
                view = recycler.getViewForPosition(pos);
                requestedItems++;
                measureChildWithMargins(view, 0, 0);
                int viewHeight = getDecoratedMeasuredHeight(view);
                int viewWidth = getDecoratedMeasuredWidth(view);

                //if new view doesn't fit in row and it isn't only one view (we have to layout views with big width somewhere)
                if (viewLeft > 0 && viewLeft + viewWidth > getWidth()) {

                    //layout previously calculated row
                    layoutRow(rowViews, viewTop, maxBottom, 0, false);

                    //go to next row, increase top coordinate, reset left
                    viewLeft = 0;
                    viewTop = maxBottom;

                    calcRecyclerCacheSize(recycler, rowSize);

                    //clear row data
                    rowViews.clear();
                    rowSize = 0;
                }

                if (viewTop > getHeight()) {
//                    Log.w("fill up", "reached end of visible bounds");
                    /* reached end of visible bounds, exit.
                    recycle view, which was requested previously
                     */
                    recycler.recycleView(view);
                    recycledItems++;
                    break;
                }

                rowSize++;

                Rect viewRect = new Rect(viewLeft, viewTop, viewLeft + viewWidth, viewTop + viewHeight);
                rowViews.add(new Pair<>(viewRect, view));

                viewLeft = viewRect.right;
                maxBottom = Math.max(maxBottom, viewRect.bottom);

                pos++;

            } else {

                int cachedTop = getDecoratedTop(view);
                if (getHeight() < cachedTop) {
                    //we reached bottom of visible children
                    break;
                }

                attachView(view);
                highestViewTop = Math.min(highestViewTop, cachedTop);

                viewCache.remove(pos);

                maxBottom = Math.max(maxBottom, getDecoratedBottom(view));

                viewLeft = getDecoratedRight(view);

                if (!(viewLeft == 0 || viewLeft + getDecoratedMeasuredWidth(view) <= getWidth())) {
                    //new row in cached views
                    viewTop = maxBottom;
                }

                pos++;
            }

        }

        Log.d("fillDown", "reattached items = " + (startCacheSize - viewCache.size() + " : requested items = " + requestedItems + " recycledItems = " + recycledItems));

        //layout last row
        layoutRow(rowViews, viewTop, maxBottom, 0, false);
    }

    /** recycler should contain all recycled views from a longest row, not just 2 holders by default*/
    private void calcRecyclerCacheSize(RecyclerView.Recycler recycler, int rowSize) {
        maxViewsInRow = Math.max(rowSize, maxViewsInRow);
        recycler.setViewCacheSize((int) (maxViewsInRow * FAST_SCROLLING_COEFFICIENT));
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = scrollVerticallyInternal(dy);
        offsetChildrenVertical(-dy);
        View anchorView = getAnchorVisibleTopLeftView();
        if (anchorView != null && getPosition(anchorView) == 0) {
            //todo refactor it, blinking now without animation. Workaround to fix start position of items if some items have been added after initialization
            detachAndScrapAttachedViews(recycler);
        }

        fill(recycler, anchorView);
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
    @Nullable
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
                if (getPosition(view) != -1) {
                    topLeft = view;
                    break;
                }
            }
        }

        return topLeft;
    }

    public void scrollToPosition(int position) {
        if (position >= getItemCount()) {
            Log.e("span layout manager", "Cannot scroll to " + position + ", item count "+getItemCount());
            return;
        }

        //Trigger a new view layout
        requestLayout();
    }

}
