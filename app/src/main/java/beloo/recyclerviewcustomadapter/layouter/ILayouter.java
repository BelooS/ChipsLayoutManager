package beloo.recyclerviewcustomadapter.layouter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Iterator;

import beloo.recyclerviewcustomadapter.SpanLayoutManager;

public interface ILayouter {
    void calculateView(View view);

    void layoutRow();
    void placeView(View view);
    void onAttachView(View view);

    boolean isFinishedLayouting();

    //todo ref this
    boolean shouldLayoutRow();

    int getPreviousRowSize();

    AbstractPositionIterator positionIterator();
}
