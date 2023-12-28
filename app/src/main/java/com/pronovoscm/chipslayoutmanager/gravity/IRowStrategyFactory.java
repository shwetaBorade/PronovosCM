package com.pronovoscm.chipslayoutmanager.gravity;

import com.pronovoscm.chipslayoutmanager.RowStrategy;

public interface IRowStrategyFactory {
    IRowStrategy createRowStrategy(@RowStrategy int rowStrategy);
}
