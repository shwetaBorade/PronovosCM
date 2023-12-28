package com.pronovoscm.chipslayoutmanager.layouter.placer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

class DisappearingViewAtStartPlacer extends AbstractPlacer {

    DisappearingViewAtStartPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addDisappearingView(view, 0);

    }
}
