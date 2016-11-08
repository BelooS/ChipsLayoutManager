package com.beloo.widget.chipslayoutmanager.layouter.placer;

public interface IPlacerFactory {
    IPlacer getAtStartPlacer();
    IPlacer getAtEndPlacer();
}
