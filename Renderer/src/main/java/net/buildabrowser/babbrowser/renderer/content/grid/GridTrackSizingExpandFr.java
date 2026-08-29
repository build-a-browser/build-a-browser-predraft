package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericFlexibleUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;

public final class GridTrackSizingExpandFr {
  
  private GridTrackSizingExpandFr() {}

  public static void expandFlexibleTracks(
    Grid grid,
    GridDirection direction,
    List<GridItem> items,
    LayoutConstraint layoutConstraint
  ) {
    if (
      layoutConstraint.type().equals(LayoutConstraintType.MIN_CONTENT)
    ) return;


    boolean isVertical = direction == GridDirection.ROW;
    float gap = GenericFlexibleUtil.mainGap(
      grid.gridBox(), isVertical, layoutConstraint);

    float flexFraction = layoutConstraint.isBounded() ?
      findDefiniteFlexFraction(
        grid, direction, layoutConstraint.value(), gap) :
      findIndefiniteFlexFraction(
        grid, direction, items, gap);
    for (GridTrack track: grid.tracks(direction)) {
      if (!isFlexible(track)) continue;
      assert track.baseSize().isBounded();
      float newBase = flexFactor(track) * flexFraction;
      if (newBase > track.baseSize().value()) {
        track.setBaseSize(LayoutConstraint.of(newBase));
      }
    }
  }

  private static float findDefiniteFlexFraction(
    Grid grid,
    GridDirection direction,
    float availableGridSpace,
    float gap
  ) {
    return findTheSizeOfAnFr(
      List.of(grid.tracks(direction)),
      availableGridSpace, gap);
  }

  private static float findIndefiniteFlexFraction(
    Grid grid,
    GridDirection direction,
    List<GridItem> items,
    float gap
  ) {
    // TODO: Use max width instead when needed
    float maximumFlexFraction = 0;
    for (GridTrack track: grid.tracks(direction)) {
      if (!isFlexible(track)) continue;
      float flexFactor = flexFactor(track);
      if (flexFactor < 1) {
        flexFactor = 1;
      }
      assert track.baseSize().isBounded();
      float trackFlexFraction = track.baseSize().value() / flexFactor;
      maximumFlexFraction = Math.max(maximumFlexFraction, trackFlexFraction);
    }

    List<GridTrack> tracks = new ArrayList<>();
    for (GridItem item: items) {
      tracks.clear();

      boolean spansFlexible = false;
      for (
        int i = item.lineStart(direction);
        i < item.lineEnd(direction);
        i++
      ) {
        GridTrack track = grid.track(i, direction);
        tracks.add(track);
        if (isFlexible(track)) {
          spansFlexible = true;
        }
      }

      if (!spansFlexible) continue;

      float maxContent = GridItemContributions
        .maxContentContribution(item, grid, direction).value();
      float itemFlexFraction = findTheSizeOfAnFr(tracks, maxContent, gap);
      maximumFlexFraction = Math.max(maximumFlexFraction, itemFlexFraction);
      // TODO: Respect min-width/height
    }

    for (GridTrack track: grid.tracks(direction)) {
      if (!isFlexible(track)) continue;
      assert track.baseSize().isBounded();
      float newSize = maximumFlexFraction * flexFactor(track);
      if (newSize > track.baseSize().value()) {
        track.setBaseSize(LayoutConstraint.of(newSize));
      }
    }

    return maximumFlexFraction;
  }

  private static float findTheSizeOfAnFr(
    List<GridTrack> tracks,
    float spaceToFill,
    float gap
  ) {
    float leftoverSpace = spaceToFill;
    if (tracks.size() > 1) {
      leftoverSpace -= (tracks.size() - 1) * gap;
    }
    for (GridTrack track: tracks) {
      if (isFlexible(track)) continue;
      assert track.baseSize().isBounded();
      leftoverSpace -= track.baseSize().value();
    }

    float flexFactorSum = 0;
    for (GridTrack track: tracks) {
      if (!isFlexible(track)) continue;
      flexFactorSum += flexFactor(track);
    }
    if (flexFactorSum < 1) {
      flexFactorSum = 1;
    }

    float hypotheticalFrSize = leftoverSpace / flexFactorSum;
    // TODO: Restart if needed
    return hypotheticalFrSize;
  }

  private static boolean isFlexible(GridTrack track) {
    return GridTrackSizingUtil.isFlexibleSizingFunction(
      track.maxTrackSizingFunction());
  }

  private static float flexFactor(GridTrack track) {
    return ((LengthValue) track.maxTrackSizingFunction())
      .value().floatValue();
  }

}
