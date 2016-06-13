package beloo.recyclerviewcustomadapter.layouter;

import beloo.recyclerviewcustomadapter.SpanLayoutManager;

public class LayouterFactory {

    private SpanLayoutManager layoutManager;

    public LayouterFactory(SpanLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public ILayouter getUpLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight, boolean isRTL) {
        return isRTL ?
                new RTLUpLayouter(layoutManager, anchorTop, anchorLeft, anchorBottom) :
                new LTRUpLayouter(layoutManager, anchorTop, anchorBottom, anchorRight);
    }

    public ILayouter getDownLayouter(int anchorTop, int anchorLeft, int anchorBottom, int anchorRight, boolean isRTL) {
        return isRTL ?
                //down layouting should start from left point of anchor view to left point of container
                new RTLDownLayouter(layoutManager, anchorTop, anchorBottom, layoutManager.getWidth()) :
                //down layouting should start from right point of anchor view to right point of container
                new LTRDownLayouter(layoutManager, anchorTop, 0, anchorBottom);
    }
}
