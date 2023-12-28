package com.pronovoscm.chipslayoutmanager.util.log;

import com.pronovoscm.chipslayoutmanager.anchor.AnchorViewState;

public interface IFillLogger {

    void onStartLayouter(int startPosition);

    void onItemRequested();

    void onItemRecycled();

    void onFinishedLayouter();

    void onAfterLayouter();

    void onRemovedAndRecycled(int position);

    void onAfterRemovingViews();

    void onBeforeLayouter(AnchorViewState state);
}

