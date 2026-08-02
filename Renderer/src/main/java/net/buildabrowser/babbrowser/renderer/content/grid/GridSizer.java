package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackListValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNumberComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatValue;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class GridSizer {
  
  private GridSizer() {}

  public static void sizeGrid(
    Grid grid,
    PropertyContainer properties,
    LayoutContext layoutContext,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    int gridWidth = sizeExplicitDimension(
      (GridTrackListValue) properties.get(CSSProperty.GRID_TEMPLATE_ROWS),
      layoutContext, widthConstraint);
    int gridHeight = sizeExplicitDimension(
      (GridTrackListValue) properties.get(CSSProperty.GRID_TEMPLATE_COLUMNS),
      layoutContext, heightConstraint);

    if (
      properties.get(CSSProperty.GRID_TEMPLATE_AREAS)
        instanceof GridTemplateAreasValue gridTemplateAreasValue
    ) {
      int templateHeight = gridTemplateAreasValue.rows().size();
      gridHeight = Math.max(gridHeight, templateHeight);
      if (templateHeight > 0) {
        gridWidth = Math.max(gridWidth, gridTemplateAreasValue.rows().get(0).cellNames().size());
      }
    }

    grid.resizeExplicit(GridSpan.create(1, gridWidth, 1, gridHeight));
  }

  private static int sizeExplicitDimension(
    GridTrackListValue tracks,
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint
  ) {
    int dimSize = 0;
    LayoutConstraint fixedSize = LayoutConstraint.of(0);
    for (GridTrackValue track: tracks.tracks()) {
      if (track.sizeOrRepeat() instanceof GridRepeatValue gridRepeatValue) {
        dimSize += sizeNumberRepeatValue(gridRepeatValue);
        LayoutConstraint repeatSize = sizeNumberRepeatValueLength(
          gridRepeatValue, layoutContext, parentConstraint);
        fixedSize = plusFixedSize(fixedSize, repeatSize);
      } else {
        dimSize++;
        LayoutConstraint trackSize = GridTrackSizer.sizeFixed(
          layoutContext, parentConstraint, track.sizeOrRepeat(), false);
        fixedSize = plusFixedSize(fixedSize, trackSize);
      }
    }

    if (tracks.repeat() instanceof GridRepeatValue repeatValue) {
      if (repeatValue.repeatTimesValue() instanceof GridRepeatNumberComponent) {
        dimSize += sizeNumberRepeatValue(repeatValue);
      } else if (parentConstraint.isBounded()) {
        LayoutConstraint remainingConstraint = LayoutConstraint.of(
          parentConstraint.value() - fixedSize.value());
        dimSize += sizeAutoRepeatValue(
          repeatValue, layoutContext, remainingConstraint);
      }
    }

    return Math.max(1, dimSize);
  }

  private static int sizeNumberRepeatValue(GridRepeatValue repeatValue) {
    int numRepeats = ((GridRepeatNumberComponent) repeatValue.repeatTimesValue()).numRepeats();
    return numRepeats * repeatValue.tracks().tracks().size();
  }

  private static int sizeAutoRepeatValue(
    GridRepeatValue repeatValue,
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint
  ) {
    LayoutConstraint fixedSize = LayoutConstraint.of(0);
    for (GridTrackValue track: repeatValue.tracks().tracks()) {
      LayoutConstraint trackSize = GridTrackSizer.sizeFixed(
        layoutContext, parentConstraint, track.sizeOrRepeat(), true);
      fixedSize = plusFixedSize(fixedSize, trackSize);
    }

    return
      (int) (parentConstraint.value() / fixedSize.value())
      * repeatValue.tracks().tracks().size();
  }

  private static LayoutConstraint sizeNumberRepeatValueLength(
    GridRepeatValue repeatValue,
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint
  ) {
    LayoutConstraint fixedSize = LayoutConstraint.of(0);
    int numRepeats = ((GridRepeatNumberComponent) repeatValue.repeatTimesValue()).numRepeats();
    for (GridTrackValue track: repeatValue.tracks().tracks()) {
      LayoutConstraint trackSize = GridTrackSizer.sizeFixed(
        layoutContext, parentConstraint, track.sizeOrRepeat(), false);
      fixedSize = plusFixedSize(fixedSize, trackSize);
    }

    return LayoutConstraint.of(numRepeats * fixedSize.value());
  }

  private static LayoutConstraint plusFixedSize(
    LayoutConstraint fixedSize,
    LayoutConstraint trackSize
  ) {
    if (!fixedSize.isBounded()) return trackSize;
    if (!trackSize.isBounded()) return fixedSize;
    return LayoutConstraint.of(trackSize.value() + fixedSize.value());
  }

}
