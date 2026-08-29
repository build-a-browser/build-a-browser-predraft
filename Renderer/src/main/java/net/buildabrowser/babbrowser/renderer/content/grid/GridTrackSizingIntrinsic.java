package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.isFlexibleSizingFunction;
import static net.buildabrowser.babbrowser.renderer.content.grid.GridTrackSizingUtil.isIntrinsicSizingFunction;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;

public final class GridTrackSizingIntrinsic {

  private static final TrackFilter FILTER_INTRINSIC_MIN
    = track -> isIntrinsicSizingFunction(track.minTrackSizingFunction());
  private static final TrackFilter FILTER_MIN_MAX_MIN
    = track -> track.minTrackSizingFunction().equals(SizeValue.MIN_CONTENT)
      || track.minTrackSizingFunction().equals(SizeValue.MAX_CONTENT);
  private static final TrackFilter FILTER_AUTO_MAX_MIN
    // TODO: Should this also include indeterminate fixed functions?
    = track -> track.minTrackSizingFunction().equals(CSSValue.AUTO)
      || track.minTrackSizingFunction().equals(SizeValue.MAX_CONTENT);
  private static final TrackFilter FILTER_MAX_MIN
    = track -> track.minTrackSizingFunction().equals(SizeValue.MAX_CONTENT);
  private static final TrackFilter FILTER_INTRINSIC_MAX
    = track -> isIntrinsicSizingFunction(track.maxTrackSizingFunction());
  private static final TrackFilter FILTER_MAX_MAX
    = track -> track.maxTrackSizingFunction().equals(SizeValue.MAX_CONTENT);

  private static final TrackFilter2 FILTER_FOR_MIN_CONTRIBUTIONS
    = new TrackFilter2(track -> isIntrinsicSizingFunction(adjustedMax(track)), true);
  private static final TrackFilter2 FILTER_FOR_MAX_CONTRIBUTIONS
    = new TrackFilter2(track -> adjustedMax(track).equals(SizeValue.MAX_CONTENT), true);
  private static final TrackFilter2 FILTER_FOR_GROWTH_CONTRIBUTIONS
    = new TrackFilter2(track -> isIntrinsicSizingFunction(adjustedMax(track)), false);
  private static final TrackFilter2 FILTER_FOR_NONE
    = new TrackFilter2(track -> false, false);

  private GridTrackSizingIntrinsic() {}

  public static void resolveIntrinsicTrackSizes(
    Grid grid,
    List<GridItem> items,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    // TODO: Shim baseline-aligned items
    List<GridItem> accommodatedItems = new ArrayList<>(items.size());

    int maxSpan = maxSpan(items, direction);
    for (int curSpan = 1; curSpan <= maxSpan; curSpan++) {
      accommodatedItems.clear();
      for (GridItem item: items) {
        int itemSpan = item.lineEnd(direction) - item.lineStart(direction);
        if (itemSpan != curSpan) continue;
        if (spansFlexible(grid, item, direction)) continue;
        accommodatedItems.add(item);
      }

      increaseSizesForItems(
        grid, direction, accommodatedItems, parentConstraint);
    }

    for (int curSpan = 1; curSpan <= maxSpan; curSpan++) {
      accommodatedItems.clear();
      for (GridItem item: items) {
        int itemSpan = item.lineEnd(direction) - item.lineStart(direction);
        if (itemSpan != curSpan) continue;
        if (!spansFlexible(grid, item, direction)) continue;
        accommodatedItems.add(item);
      }

      // TODO: Special handling for flexible tracks
      increaseSizesForItems(
        grid, direction, accommodatedItems, parentConstraint);
    }

    // TODO: Handle items that do span multiple tracks

    for (GridTrack track: grid.tracks(direction)) {
      if (!track.growthLimit().isBounded()) {
        track.setGrowthLimit(track.baseSize());
      }
    }
  }

  private static void increaseSizesForItems(
    Grid grid,
    GridDirection direction,
    List<GridItem> accommodatedItems,
    LayoutConstraint sizingConstraint
  ) {
    // TODO: Substitute min-content contributions
    distributeExtraSpace(
      grid, direction, accommodatedItems,
      false, FILTER_INTRINSIC_MIN,
      GridItemContributions::minimumContribution,
      FILTER_FOR_MIN_CONTRIBUTIONS);

    distributeExtraSpace(
      grid, direction, accommodatedItems,
      false, FILTER_MIN_MAX_MIN,
      GridItemContributions::minContentContribution,
      FILTER_FOR_MIN_CONTRIBUTIONS);

    if (isLikeMaxContent(sizingConstraint)) {
      distributeExtraSpace(
        grid, direction, accommodatedItems,
        false, FILTER_AUTO_MAX_MIN,
        GridItemContributions::limitedMaxContentContribution,
        FILTER_FOR_NONE);
    }

    distributeExtraSpace(
      grid, direction, accommodatedItems,
      false, FILTER_MAX_MIN,
      GridItemContributions::maxContentContribution,
      FILTER_FOR_MAX_CONTRIBUTIONS);

    increaseUnderflowGrowthLimits(grid, direction);

    distributeExtraSpace(
      grid, direction, accommodatedItems,
      true, FILTER_INTRINSIC_MAX,
      GridItemContributions::minContentContribution,
      FILTER_FOR_GROWTH_CONTRIBUTIONS);

    distributeExtraSpace(
      grid, direction, accommodatedItems,
      true, FILTER_MAX_MAX,
      GridItemContributions::maxContentContribution,
      FILTER_FOR_GROWTH_CONTRIBUTIONS);
  }

  private static void increaseUnderflowGrowthLimits(
    Grid grid, GridDirection direction
  ) {
    for (GridTrack track: grid.tracks(direction)) {
      if (
        track.baseSize().isBounded()
        && track.growthLimit().isBounded()
        && track.growthLimit().value() < track.baseSize().value()
      ) {
        track.setGrowthLimit(track.baseSize());
      }
    }
  }

  private static void distributeExtraSpace(
    Grid grid,
    GridDirection direction,
    List<GridItem> accommodatedItems,
    boolean isGrowth,
    TrackFilter affectedTracksFilter,
    ItemContribution sizeContributionGetter,
    TrackFilter2 accommodatedFilter
  ) {
    // TODO: Skip items with FR
    for (GridItem item: accommodatedItems) {
      float space = spaceToDistribute(
        grid, direction, isGrowth, sizeContributionGetter, item);

      space = distributeAsNeeded(
        grid, direction, item, isGrowth, space,
        affectedTracksFilter);

      TrackFilter invertedAffectedTracksFilter
        = a -> !affectedTracksFilter.filter(a);
      if (
        space > 0
        && spansATrack(grid, direction, item, affectedTracksFilter)
      ) {
        space = distributeAsNeeded(
          grid, direction, item, isGrowth, space,
          invertedAffectedTracksFilter);
      }

      // TODO: Distribute space beyond limits

      // NOSPEC: It says affected tracks, but there may have
      // been an increase for non-affected tracks
      for (GridTrack track: grid.tracks(direction)) {
        track.finalizeItemIncurredIncrease();
      }
    }

    for (GridTrack track: grid.tracks(direction)) {
      float increase = track.plannedIncrease();
      LayoutConstraint affectedConstraint =
        isGrowth ? track.growthLimit() : track.baseSize();
      assert affectedConstraint.isBounded() || isGrowth;
      if (affectedConstraint.isBounded()) {
        float newSize = affectedConstraint.value() + increase;
        LayoutConstraint newConstraint = LayoutConstraint.of(newSize);
        if (isGrowth) {
          track.setGrowthLimit(newConstraint);
        } else {
          track.setBaseSize(newConstraint);
        }
      } else {
        assert track.baseSize().isBounded();
        float newSize = track.baseSize().value() + increase;
        track.setGrowthLimit(LayoutConstraint.of(newSize));
        track.setInfinitelyGrowable(true);
      }
    }
  }

  private static float spaceToDistribute(
    Grid grid,
    GridDirection direction,
    boolean isGrowth,
    ItemContribution sizeContributionGetter,
    GridItem item
  ) {
    LayoutConstraint sizeContribution = sizeContributionGetter
      .get(item, grid, direction);
    
    // TODO: Can sizeContribution be unbounded at this point?
    assert sizeContribution.isBounded();
    if (!sizeContribution.isBounded()) return 0;

    float space = sizeContribution.value();
    int span = item.lineEnd(direction) - item.lineStart(direction);
    space -= GridTrackSizingUtil.spanGap(
      grid.gridBox(), direction, span, LayoutConstraint.AUTO);

    for (
      int i = item.lineStart(direction);
      i < item.lineEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      track.setFrozen(false);
      LayoutConstraint trackSize = isGrowth ? track.growthLimit() : track.baseSize();
      if (!trackSize.isBounded()) {
        trackSize = track.baseSize();
      }
      assert trackSize.isBounded();
      space -= trackSize.value();
    }
    return Math.max(0, space);
  }

  private static float distributeAsNeeded(
    Grid grid,
    GridDirection direction,
    GridItem relatedItem,
    boolean isGrowth,
    float space,
    TrackFilter affectedTracksFilter
  ) {
    int[] unfrozenCount = countUnfrozen(
      grid, direction, relatedItem, affectedTracksFilter);
    while (unfrozenCount[0] > 0 && space > 0.0001) {
      space = distributeSpace(
        grid, direction, relatedItem, isGrowth,
        affectedTracksFilter,
        unfrozenCount, space);
    }

    return space;
  }

  private static int[] countUnfrozen(
    Grid grid,
    GridDirection direction,
    GridItem relatedItem,
    TrackFilter affectedTracksFilter
  ) {
    int unfrozenCount = 0;
    for (
      int i = relatedItem.lineStart(direction);
      i < relatedItem.lineEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      if (!affectedTracksFilter.filter(track)) continue;

      track.setFrozen(false);
      unfrozenCount++;
    }

    return new int[] { unfrozenCount };
  }

  private static float distributeSpace(
    Grid grid,
    GridDirection direction,
    GridItem relatedItem,
    boolean isGrowth,
    TrackFilter affectedTracksFilter,
    int[] unfrozenCount,
    float space
  ) {
    float preferredAmount = space / unfrozenCount[0];
    // TODO: The spec doesn't seem to explicitly state that it's only the affected tracks that *span the item*
    // but that makes the most sense
    for (
      int i = relatedItem.lineStart(direction);
      i < relatedItem.lineEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      if (track.frozen()) continue;
      if (!affectedTracksFilter.filter(track)) continue;

      // TODO: infinitely-growable is supposed to apply only when isGrowth,
      // but breaks sizing. It seems likely that "Update the tracks' affected sizes"
      // is supposed to only set infinitely-growable in some cases, but I'm not sure.
      // (because keep in mind that non-affected tracks can also grow, so that step
      // can't be filtered to just affected tracks)
      LayoutConstraint growthLimit =
        /*isGrowth && */track.isInfinitelyGrowable() ?
          LayoutConstraint.AUTO :
          track.growthLimit();
        
      // TODO: Cap by fit-content
      LayoutConstraint affectedSize = isGrowth ?
        growthLimit : track.baseSize();

      assert affectedSize.isBounded() || isGrowth;
      if (!affectedSize.isBounded()) {
        affectedSize = track.baseSize();
        assert affectedSize.isBounded();
      }
      float adjustedSize =
        affectedSize.value()
        + track.itemIncurredIncrease()
        + preferredAmount;

      if (
        growthLimit.isBounded()
        && adjustedSize >= growthLimit.value()
      ) {
        float adjustment = Math.max(0, 
          growthLimit.value()
          - affectedSize.value()
          - track.itemIncurredIncrease());
        track.increaseItemIncurredIncrease(adjustment);
        space -= adjustment;
        track.setFrozen(true);
        unfrozenCount[0]--;
      } else {
        track.increaseItemIncurredIncrease(preferredAmount);
        space -= preferredAmount;
      }
      // TODO: Cap by fit content
    }

    return space;
  }

  private static int maxSpan(
    List<GridItem> items,
    GridDirection direction
  ) {
    int maxSpan = 1;
    for (GridItem item: items) {
      maxSpan = Math.max(maxSpan,
        item.lineEnd(direction) - item.lineStart(direction));
    }

    return maxSpan;
  }

  private static boolean spansATrack(
    Grid grid,
    GridDirection direction,
    GridItem item,
    TrackFilter affectedTracksFilter
  ) {
    for (
      int i = item.lineStart(direction);
      i < item.lineEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      boolean isTarget = affectedTracksFilter.filter(track);
      if (isTarget) return true;
    }

    return false;
  }

  private static boolean spansFlexible(
    Grid grid,
    GridItem item,
    GridDirection direction
  ) {
    return spansATrack(
      grid, direction, item,
      track -> isFlexibleSizingFunction(
        track.maxTrackSizingFunction()));
  }

  private static CSSValue adjustedMax(GridTrack track) {
    // TODO: Handle fit-content
    return track.maxTrackSizingFunction();
  }

  private static boolean isLikeMaxContent(LayoutConstraint sizingConstraint) {
    return
      !sizingConstraint.isBounded()
      && !sizingConstraint.type().equals(LayoutConstraintType.MIN_CONTENT);
  }

  private static interface TrackFilter {

    boolean filter(GridTrack track);

  }

  private static record TrackFilter2(
    TrackFilter filter,
    boolean fallback
  ) {}

  private static interface ItemContribution {

    LayoutConstraint get(
      GridItem item, Grid grid, GridDirection direction
    );

  }
  
}
