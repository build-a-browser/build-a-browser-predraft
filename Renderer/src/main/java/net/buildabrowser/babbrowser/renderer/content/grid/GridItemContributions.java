package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class GridItemContributions {
  
  private GridItemContributions() {}

  public static LayoutConstraint minimumContribution(
    GridItem item, Grid grid, GridDirection direction
  ) {
    // TODO: Respect min-width
    return minContentContribution(item, grid, direction);
  }

  public static LayoutConstraint minContentContribution(
    GridItem item, Grid grid, GridDirection direction
  ) {
    float value = wrapBox(
      item, direction,
      minContentRaw(item, grid, direction));
    return LayoutConstraint.of(value);
  }

  public static LayoutConstraint maxContentContribution(
    GridItem item, Grid grid, GridDirection direction
  ) {  
    float value = wrapBox(
      item, direction,
      maxContentRaw(item, grid, direction));
    return LayoutConstraint.of(value);
  }

  public static LayoutConstraint limitedMaxContentContribution(
    GridItem item, Grid grid, GridDirection direction
  ) {
    // TODO: Clamp by min/max
    return maxContentContribution(item, grid, direction);
  }

  private static float wrapBox(
    GridItem item, GridDirection direction, float dim
  ) {
    ElementBoxDimensions dimensions = item.box().dimensions();
    return dim + switch (direction) {
      case COLUMN -> dimensions.decorWidth();
      case ROW -> dimensions.decorHeight();
      default -> throw new UnsupportedOperationException(
        "Unrecognize grid direction: " + direction);
    };
  }

  private static float minContentRaw(
    GridItem item, Grid grid, GridDirection direction
  ) {
    return switch (direction) {
      case COLUMN -> EBDimensionsUtil.preferredMinWidthConstraint(item.box());
      case ROW -> computeContentRow(item, grid);
      default -> throw new UnsupportedOperationException(
        "Unrecognize grid direction: " + direction);
    };
  }

  private static float maxContentRaw(
    GridItem item, Grid grid, GridDirection direction
  ) {
    return switch (direction) {
      case COLUMN -> EBDimensionsUtil.preferredWidthConstraint(item.box());
      case ROW -> computeContentRow(item, grid);
      default -> throw new UnsupportedOperationException(
        "Unrecognize grid direction: " + direction);
    };
  }

  private static float computeContentRow(
    GridItem item, Grid grid
  ) {
    float inlineSize = 0;
    for (
      int i = item.colLineStart();
      i < item.colLineEnd();
      i++
    ) {
      LayoutConstraint size = grid.track(i, GridDirection.COLUMN).baseSize();
      assert size.isBounded();
      inlineSize += size.value();
    }

    int span = item.colLineEnd() - item.colLineStart();
    inlineSize += GridTrackSizingUtil.spanGap(
      grid.gridBox(), GridDirection.COLUMN, span, LayoutConstraint.AUTO);

    ElementBoxDimensions dimensions = item.box().dimensions();
      float decorWidthM = dimensions.decorWidth() 
        + dimensions.getComputedMargin()[2] 
        + dimensions.getComputedMargin()[3];
      float contentWidth = Math.max(0, inlineSize - decorWidthM);

    UnmanagedBoxFragment<?> fragment = item.box().layout(
      LayoutConstraint.of(contentWidth),
      LayoutConstraint.AUTO);
    return fragment.height(Measurement.CONTENT);
  }

}
