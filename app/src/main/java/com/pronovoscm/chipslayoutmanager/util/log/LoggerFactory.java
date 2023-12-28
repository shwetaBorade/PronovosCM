package com.pronovoscm.chipslayoutmanager.util.log;

import androidx.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

public class LoggerFactory {
    @NonNull
    public IFillLogger getFillLogger(SparseArray<View> viewCache) {
        return new FillLogger(viewCache);
    }

}
