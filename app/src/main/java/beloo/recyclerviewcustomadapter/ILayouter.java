package beloo.recyclerviewcustomadapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

interface ILayouter {
    void calculateView(View view, RecyclerView.LayoutManager layoutManager);

    void layoutRow(SpanLayoutManager layoutManager);
    void placeView(View view, RecyclerView.LayoutManager layoutManager);
    void onAttachView(View view, RecyclerView.LayoutManager layoutManager);

    boolean isFinishedLayouting();

    //todo ref this
    boolean shouldLayoutRow();
}
