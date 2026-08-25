package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridMinMaxValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackListValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNumberComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatValue;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class GridSizer {
  
  private GridSizer() {}

  // TODO: Also need to account for gap
  public static void sizeGridAndPlaceLines(
    Grid grid,
    PropertyContainer properties,
    LayoutContext layoutContext,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    List<GridTrackValue> colTracks = new ArrayList<>();
    CSSValue gridTemplateColumns = properties.get(CSSProperty.GRID_TEMPLATE_COLUMNS);
    int gridWidth = gridTemplateColumns.equals(CSSValue.NONE) ?
      0 : sizeExplicitDimension(
        (GridTrackListValue) gridTemplateColumns,
        colTracks, layoutContext, widthConstraint);
    
    List<GridTrackValue> rowTracks = new ArrayList<>();
    CSSValue gridTemplateRows = properties.get(CSSProperty.GRID_TEMPLATE_ROWS);
    int gridHeight = gridTemplateRows.equals(CSSValue.NONE) ?
      0 : sizeExplicitDimension(
        (GridTrackListValue) gridTemplateRows,
        rowTracks, layoutContext, heightConstraint);

    if (
      properties.get(CSSProperty.GRID_TEMPLATE_AREAS)
        instanceof GridTemplateAreasValue gridTemplateAreasValue
    ) {
      for (GridArea area: gridTemplateAreasValue.areas()) {
        gridWidth = Math.max(gridWidth, area.x() + area.w() - 1);
        gridHeight = Math.max(gridHeight, area.y() + area.h() - 1);
      }
    }

    grid.resizeExplicit(GridSpan.create(1, gridWidth, 1, gridHeight));

    useTrackValues(grid, colTracks, Grid::column, Grid::columnLine);
    useTrackValues(grid, rowTracks, Grid::row, Grid::rowLine);
  }

  private static int sizeExplicitDimension(
    GridTrackListValue tracks,
    List<GridTrackValue> trackValues,
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint
  ) {
    int dimSize = 0;
    LayoutConstraint fixedSize = LayoutConstraint.of(0);
    for (GridTrackValue track: tracks.tracks()) {
      if (track.sizeOrRepeat() instanceof GridRepeatValue gridRepeatValue) {
        trackValues.add(track);
        dimSize += sizeNumberRepeatValue(gridRepeatValue, trackValues);
        LayoutConstraint repeatSize = sizeNumberRepeatValueLength(
          gridRepeatValue, layoutContext, parentConstraint);
        fixedSize = plusFixedSize(fixedSize, repeatSize);
      } else {
        trackValues.add(track);
        dimSize++;
        LayoutConstraint trackSize = sizeFixed(
          layoutContext, parentConstraint, track.sizeOrRepeat(), false);
        fixedSize = plusFixedSize(fixedSize, trackSize);
      }
    }

    if (tracks.repeat() instanceof GridRepeatValue repeatValue) {
      if (repeatValue.repeatTimesValue() instanceof GridRepeatNumberComponent) {
        dimSize += sizeNumberRepeatValue(repeatValue, trackValues);
      } else if (parentConstraint.isBounded()) {
        LayoutConstraint remainingConstraint = LayoutConstraint.of(
          parentConstraint.value() - fixedSize.value());
        dimSize += sizeAutoRepeatValue(
          repeatValue, trackValues, layoutContext, remainingConstraint);
      }
    }

    return Math.max(1, dimSize);
  }

  private static int sizeNumberRepeatValue(
    GridRepeatValue repeatValue,
    List<GridTrackValue> trackValues
  ) {
    int numRepeats = ((GridRepeatNumberComponent) repeatValue.repeatTimesValue()).numRepeats();
    repeatAddTrackValues(repeatValue, trackValues, numRepeats);
    return numRepeats * repeatValue.tracks().tracks().size();
  }

  private static int sizeAutoRepeatValue(
    GridRepeatValue repeatValue,
    List<GridTrackValue> trackValues,
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint
  ) {
    LayoutConstraint fixedSize = LayoutConstraint.of(0);
    for (GridTrackValue track: repeatValue.tracks().tracks()) {
      LayoutConstraint trackSize = sizeFixed(
        layoutContext, parentConstraint, track.sizeOrRepeat(), true);
      fixedSize = trackSize.isBounded() ?
        plusFixedSize(fixedSize, trackSize) :
        LayoutConstraint.AUTO;
    }

    int numRepeats = fixedSize.isBounded() ?
      Math.max(1, (int) (parentConstraint.value() / fixedSize.value())) : 1;
    repeatAddTrackValues(repeatValue, trackValues, numRepeats);
    return numRepeats * repeatValue.tracks().tracks().size();
  }

  private static LayoutConstraint sizeNumberRepeatValueLength(
    GridRepeatValue repeatValue,
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint
  ) {
    LayoutConstraint fixedSize = LayoutConstraint.of(0);
    int numRepeats = ((GridRepeatNumberComponent) repeatValue.repeatTimesValue()).numRepeats();
    for (GridTrackValue track: repeatValue.tracks().tracks()) {
      LayoutConstraint trackSize = sizeFixed(
        layoutContext, parentConstraint, track.sizeOrRepeat(), false);
      fixedSize = plusFixedSize(fixedSize, trackSize);
    }

    return LayoutConstraint.of(numRepeats * fixedSize.value());
  }

  private static void repeatAddTrackValues(
    GridRepeatValue repeatValue,
    List<GridTrackValue> trackValues,
    int numRepeats
  ) {
    for (int i = 0; i < numRepeats; i++) {
      for (GridTrackValue track: repeatValue.tracks().tracks()) {
        trackValues.add(track);
      }
    }
  }

  private static LayoutConstraint plusFixedSize(
    LayoutConstraint fixedSize,
    LayoutConstraint trackSize
  ) {
    if (!fixedSize.isBounded()) return trackSize;
    if (!trackSize.isBounded()) return fixedSize;
    return LayoutConstraint.of(trackSize.value() + fixedSize.value());
  }

  private static void useTrackValues(
    Grid grid,
    List<GridTrackValue> trackValues,
    BiFunction<Grid, Integer, GridTrack> trackFunc,
    BiFunction<Grid, Integer, GridLine> lineFunc
  ) {
    int i = 1;
    for (GridTrackValue trackValue: trackValues) {
      lineFunc.apply(grid, i).addNames(trackValue.lineNames());
      if (
        trackValue.sizeOrRepeat() instanceof GridRepeatValue
      ) continue;
      if (trackValue.sizeOrRepeat() != null) {
        trackFunc.apply(grid, i).setSizeValue(trackValue.sizeOrRepeat());
      }
      i++;
    }
  }

  public static LayoutConstraint sizeFixed(
    LayoutContext layoutContext,
    LayoutConstraint reference,
    CSSValue dimension,
    boolean isAutoRepeat
  ) {
    if (dimension instanceof GridMinMaxValue minMaxValue) {
      LayoutConstraint min = sizeFixed(layoutContext, reference, minMaxValue.min(), isAutoRepeat);
      LayoutConstraint max = sizeFixed(layoutContext, reference, minMaxValue.max(), isAutoRepeat);
      if (!max.isBounded()) {
        max = min;
      }
      if (!min.isBounded()) {
        min = max;
      }
      
      if (!max.isBounded()) {
        return isAutoRepeat ? LayoutConstraint.AUTO : LayoutConstraint.of(0);
      }

      return max.value() < min.value() ? min : max;
    }

    LayoutConstraint size = SizingUtil.evaluateBaseSize(layoutContext, reference, dimension);
    if (!isAutoRepeat && !size.isBounded()) {
      size = LayoutConstraint.of(0);
    }

    return size;
  }

}
