package com.pronovoscm.chipslayoutmanager.layouter.criteria;

import androidx.annotation.NonNull;

public interface ICriteriaFactory {
    @NonNull
    IFinishingCriteria getBackwardFinishingCriteria();

    @NonNull
    IFinishingCriteria getForwardFinishingCriteria();
}
