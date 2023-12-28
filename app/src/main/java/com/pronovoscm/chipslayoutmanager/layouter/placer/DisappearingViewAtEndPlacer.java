package com.pronovoscm.chipslayoutmanager.layouter.placer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

class DisappearingViewAtEndPlacer extends AbstractPlacer {

    DisappearingViewAtEndPlacer(RecyclerView.LayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void addView(View view) {
        getLayoutManager().addDisappearingView(view);

//        Log.i("added disappearing view, position = " + getLayoutManager().getPosition(view));
//        Log.d("name = " + ((TextView)view.findViewById(R.id.tvName)).getText().toString());
    }
}
