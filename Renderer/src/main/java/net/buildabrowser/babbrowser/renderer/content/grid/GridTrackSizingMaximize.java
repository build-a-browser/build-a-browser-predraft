package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.unusedSpace;

import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;

public final class GridTrackSizingMaximize {
  
  private GridTrackSizingMaximize() {}

  public static void maximizeTracks(
    Grid grid,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    if (
      !parentConstraint.isBounded()
      && !parentConstraint.type().equals(LayoutConstraintType.MIN_CONTENT)
    ) {
      maximizeTracksForMaxContent(grid, direction);
      return;
    }

    // TODO: If the max-width/height is less than the parent constraint, use
    // that instead when computing the free space
    float freeSpace = unusedSpace(grid, parentConstraint, direction);
    distributeAsNeeded(grid, direction, freeSpace);
  }

  private static void maximizeTracksForMaxContent(
    Grid grid, GridDirection direction
  ) {
    for (GridTrack track: grid.tracks(direction)) {
      // Growth limit is replaced based on base size during intrinsic track sizing
      assert track.growthLimit().isBounded();

      track.setBaseSize(track.growthLimit());
    }
  }

  // TODO: The remainder of this code is essentially a simplified version
  // of the same methods in GridTrackSizingImplicit. Use that instead?

  private static float distributeAsNeeded(
    Grid grid,
    GridDirection direction,
    float space
  ) {
    int[] unfrozenCount = countUnfrozen(grid, direction);
    while (unfrozenCount[0] > 0 && space > 0.0001) {
      space = distributeSpace(
        grid, direction, unfrozenCount, space);
    }

    return space;
  }

  private static int[] countUnfrozen(
    Grid grid,
    GridDirection direction
  ) {
    int unfrozenCount = 0;
    for (
      int i = grid.implicitSpan().trackStart(direction);
      i <= grid.implicitSpan().trackEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);

      track.setFrozen(false);
      unfrozenCount++;
    }

    return new int[] { unfrozenCount };
  }

  private static float distributeSpace(
    Grid grid,
    GridDirection direction,
    int[] unfrozenCount,
    float space
  ) {
    float preferredAmount = space / unfrozenCount[0];
    // TODO: The spec doesn't seem to explicitly state that it's only the affected tracks that *span the item*
    // but that makes the most sense
    for (
      int i = grid.implicitSpan().trackStart(direction);
      i <= grid.implicitSpan().trackEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      if (track.frozen()) continue;

      LayoutConstraint growthLimit = track.growthLimit();
      assert growthLimit.isBounded();
      LayoutConstraint baseSize = track.baseSize();
      assert baseSize.isBounded();

      float adjustedSize = baseSize.value() + preferredAmount;

      if (
        growthLimit.isBounded()
        && adjustedSize >= growthLimit.value()
      ) {
        float adjustment = Math.max(0, 
          growthLimit.value() - baseSize.value());
        track.setBaseSize(LayoutConstraint.of(
          baseSize.value() + adjustment));
        space -= adjustment;
        track.setFrozen(true);
        unfrozenCount[0]--;
      } else {
        track.setBaseSize(LayoutConstraint.of(
          baseSize.value() + preferredAmount));
        space -= preferredAmount;
      }
      // TODO: Cap by fit content
    }

    return space;
  }

}
