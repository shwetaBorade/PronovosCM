package com.pronovoscm.chipslayoutmanager.layouter.placer;

public interface IPlacerFactory {
    IPlacer getAtStartPlacer();
    IPlacer getAtEndPlacer();
}
