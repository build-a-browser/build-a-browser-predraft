package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue.FitContent;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class GridTrackSizingAlgorithm {

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
  
  private GridTrackSizingAlgorithm() {}

  public static void sizeGridTracks(
    Grid grid,
    List<GridItem> items,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    initializeTrackSizes(grid, parentConstraint, direction);
    resolveIntrinsicTrackSizes(grid, items, parentConstraint, direction);
    // TODO: Maximize tracks, expand flexible tracks, expand auto tracks
  }

  private static void initializeTrackSizes(
    Grid grid,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    for (
      int i = grid.implicitSpan().trackStart(direction);
      i < grid.implicitSpan().trackEnd(direction);
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
        LayoutConstraint swapSize = growthLimit;
        growthLimit = baseSize;
        baseSize = swapSize;
      }

      track.setBaseSize(baseSize);
      track.setGrowthLimit(growthLimit);
    }
  }

  private static void resolveIntrinsicTrackSizes(
    Grid grid,
    List<GridItem> items,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    // TODO: Shim baseline-aligned items
    List<GridItem> accommodatedItems = new ArrayList<>(items.size());

    int maxSpan = maxSpan(items, direction);
    for (int curSpan = 1; curSpan < maxSpan; curSpan++) {
      accommodatedItems.clear();
      for (GridItem item: items) {
        int itemSpan = item.lineEnd(direction) - item.lineStart(direction);
        if (itemSpan != curSpan) continue;
        if (spansFlexible(grid, item, direction)) continue;
        accommodatedItems.add(item);
      }

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

      distributeExtraSpace(
        grid, direction, accommodatedItems,
        false, FILTER_AUTO_MAX_MIN,
        GridItemContributions::limitedMaxContentContribution,
        FILTER_FOR_NONE);

      distributeExtraSpace(
        grid, direction, accommodatedItems,
        false, FILTER_MAX_MIN,
        GridItemContributions::maxContentContribution,
        FILTER_FOR_MAX_CONTRIBUTIONS);

      // TODO: Step 4

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
    for (GridItem item: accommodatedItems) {
      float space = spaceToDistribute(
        grid, direction, isGrowth, sizeContributionGetter, item);

      space = distributeAsNeeded(
        grid, direction, isGrowth, space,
        affectedTracksFilter);

      TrackFilter invertedAffectedTracksFilter
        = a -> !affectedTracksFilter.filter(a);
      if (space > 0) {
        space = distributeAsNeeded(
          grid, direction, isGrowth, space,
          invertedAffectedTracksFilter);
      }

      // TODO: Distribute space beyond limits


      // NOSPEC: It says affected tracks, but there may have
      // been an increase for non-affected tracks
      for (GridTrack track: grid.tracks(direction)) {
        track.finalizeItemIncurredIncrease();
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
    for (GridTrack track: grid.tracks(direction)) {
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
    boolean isGrowth,
    float space,
    TrackFilter affectedTracksFilter
  ) {
    int[] unfrozenCount = countUnfrozen(
      grid, direction, affectedTracksFilter);
    while (unfrozenCount[0] > 0 && space > 0) {
      space = distributeSpace(
        grid, direction, isGrowth,
        affectedTracksFilter,
        unfrozenCount, space);
    }

    return space;
  }

  private static int[] countUnfrozen(
    Grid grid,
    GridDirection direction,
    TrackFilter affectedTracksFilter
  ) {
    int unfrozenCount = 0;
    for (
      int i = grid.implicitSpan().trackStart(direction);
      i <= grid.implicitSpan().trackEnd(direction);
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
    boolean isGrowth,
    TrackFilter affectedTracksFilter,
    int[] unfrozenCount,
    float space
  ) {
    float preferredAmount = space / unfrozenCount[0];
    for (
      int i = grid.implicitSpan().trackStart(direction);
      i <= grid.implicitSpan().trackEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      if (track.frozen()) continue;
      if (!affectedTracksFilter.filter(track)) continue;

      // TODO: Infinitely growable?
      LayoutConstraint growthLimit = track.growthLimit();
      LayoutConstraint affectedSize = isGrowth ?
        growthLimit : track.baseSize();
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
        float adjustment =
          growthLimit.value()
          - affectedSize.value()
          - track.itemIncurredIncrease();
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

  private static boolean spansFlexible(
    Grid grid,
    GridItem item,
    GridDirection direction
  ) {
    for (
      int i = item.lineStart(direction);
      i < item.lineEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      boolean isFlexible = isFlexibleSizingFunction(
        track.maxTrackSizingFunction());
      if (isFlexible) return true;
    }

    return false;
  }

  private static CSSValue adjustedMax(GridTrack track) {
    // TODO: Handle fit-content
    return track.maxTrackSizingFunction();
  }

  private static boolean isIntrinsicSizingFunction(
    CSSValue sizingFunction
  ) {
    return
      sizingFunction.equals(CSSValue.AUTO)
      || sizingFunction instanceof SizeValue
      || sizingFunction instanceof FitContent;
  }

  private static boolean isFlexibleSizingFunction(
    CSSValue sizingFunction
  ) {
    return
      sizingFunction instanceof LengthValue lengthValue
      && LengthType.FR.equals(lengthValue.dimension());
  }

  private static LayoutConstraint evaluateFixedSize(
    Grid grid,
    LayoutConstraint parentConstraint,
    CSSValue sizingFunction
  ) {
    // TODO: Better to use the layoutContext passed to content?
    LayoutContext layoutContext = grid.gridBox().layoutContext();
    return SizingUtil.evaluateBaseSize(
      layoutContext, parentConstraint, sizingFunction);
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
