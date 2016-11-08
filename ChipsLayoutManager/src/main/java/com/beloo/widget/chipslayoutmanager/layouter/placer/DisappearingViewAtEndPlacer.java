package com.beloo.widget.chipslayoutmanager.layouter.placer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.beloo.widget.chipslayoutmanager.R;

class DisappearingViewAtEndPlacer extends AbstractPlacer {

    DisappearingViewAtEndPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addDisappearingView(view);

        Log.i(this.getClass().getSimpleName(), "added disappearing view, position = " + getLayoutManager().getPosition(view));
        Log.d(this.getClass().getSimpleName(), "name = " + ((TextView)view.findViewById(R.id.tvName)).getText().toString());
    }
}
