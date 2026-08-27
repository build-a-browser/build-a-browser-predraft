package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.evaluateFixedSize;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.isFlexibleSizingFunction;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.isIntrinsicSizingFunction;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.unusedSpace;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.align.JustifyContentValue;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class GridTrackSizingAlgorithm {
  
  private GridTrackSizingAlgorithm() {}

  public static void sizeGridTracks(
    Grid grid,
    List<GridItem> items,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    initializeTrackSizes(grid, parentConstraint, direction);
    GridTrackSizingIntrinsic.resolveIntrinsicTrackSizes(grid, items, parentConstraint, direction);
    GridTrackSizingMaximize.maximizeTracks(grid, parentConstraint, direction);
    GridTrackSizingExpandFr.expandFlexibleTracks(
      grid, direction, items, parentConstraint);
    stretchAutoTracks(grid, parentConstraint, direction);
  }

  private static void initializeTrackSizes(
    Grid grid,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    CSSProperty autoProperty = direction == GridDirection.COLUMN ?
      CSSProperty.GRID_AUTO_COLUMNS :
      CSSProperty.GRID_AUTO_ROWS;
    CSSValue autoTrackSizing = grid.gridBox().properties().get(autoProperty);

    if (autoTrackSizing instanceof ManyResult manyResult) {
      initializeAutoTrackSizes(grid, direction, manyResult);
    }

    for (
      int i = grid.implicitSpan().trackStart(direction);
      i <= grid.implicitSpan().trackEnd(direction);
      i++
    ) {
      // TODO: Extract to initializeTrackSize
      GridTrack track = grid.track(i, direction);

      // It's easier to check if it's intrinsic than fixed, so re-order the clauses
      CSSValue minFunc = track.minTrackSizingFunction();
      CSSValue maxFunc = track.maxTrackSizingFunction();
      LayoutConstraint baseSize = isIntrinsicSizingFunction(minFunc) ?
        LayoutConstraint.of(0) :
        evaluateFixedSize(grid, parentConstraint, minFunc);
      LayoutConstraint growthLimit =
        isIntrinsicSizingFunction(maxFunc) ? LayoutConstraint.AUTO :
        isFlexibleSizingFunction(maxFunc) ? LayoutConstraint.AUTO :
        evaluateFixedSize(grid, parentConstraint, maxFunc);

      if (
        baseSize.isBounded()
        && growthLimit.isBounded()
        && growthLimit.value() < baseSize.value()
      ) {
        growthLimit = baseSize;
      }

      track.setBaseSize(baseSize);
      track.setGrowthLimit(growthLimit);
    }
  }

  private static void initializeAutoTrackSizes(
    Grid grid, GridDirection direction, ManyResult manyResult
  ) {
    List<CSSValue> autoSizes = manyResult.values();

    int i = 0;
    for (
      int x = Math.max(1, grid.explicitSpan().trackEnd(direction));
      x <= grid.implicitSpan().trackEnd(direction);
      x++
    ) {
      CSSValue size = autoSizes.get(i++ % autoSizes.size());
      grid.track(x, direction).setSizeValue(size);
    }

    i = -1;
    for (
      int x = grid.explicitSpan().trackStart(direction);
      x >= grid.implicitSpan().trackStart(direction);
      x--
    ) {
      CSSValue size = autoSizes.get(i-- % autoSizes.size());
      grid.track(x, direction).setSizeValue(size);
    }
  }

  private static void stretchAutoTracks(
    Grid grid,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    PropertyContainer properties = grid.gridBox().properties();
    CSSValue alignment = direction.equals(GridDirection.COLUMN) ?
      properties.get(CSSProperty.JUSTIFY_CONTENT) :
      properties.get(CSSProperty.ALIGN_CONTENT);
    if(!(
      alignment.equals(JustifyContentValue.NORMAL)
      || alignment.equals(JustifyContentValue.STRETCH)
      || alignment.equals(AlignContentValue.NORMAL)
      || alignment.equals(AlignContentValue.STRETCH)
    )) return;

    // TODO: Use max size if constraint is indefinite
    float space = unusedSpace(grid, parentConstraint, direction);
    if (space <= 0) return;

    int autoCount = 0;
    for (GridTrack track: grid.tracks(direction)) {
      // TODO: Check the growth limit instead? An invalid sizing function could also cause auto
      // (but the spec does say max track sizing function)
      if (track.maxTrackSizingFunction().equals(CSSValue.AUTO)) {
        autoCount++;
      }
    }
    if (autoCount == 0) return;

    float spacePerTrack = space / autoCount;
    for (GridTrack track: grid.tracks(direction)) {
      if (!track.maxTrackSizingFunction().equals(CSSValue.AUTO)) continue;
      assert track.baseSize().isBounded();
      float newSize = track.baseSize().value() + spacePerTrack;
      track.setBaseSize(LayoutConstraint.of(newSize));
    }
  }

}
