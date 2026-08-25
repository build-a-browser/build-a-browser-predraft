package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.evaluateFixedSize;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.isFlexibleSizingFunction;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.isIntrinsicSizingFunction;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.unusedSpace;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
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
    for (
      int i = grid.implicitSpan().trackStart(direction);
      i <= grid.implicitSpan().trackEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      // TODO: Extract to initializeTrackSize
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

  private static void stretchAutoTracks(
    Grid grid,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
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
