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
    ElementBoxDimensions dimensions = item.itemBox().dimensions();
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
      case COLUMN -> EBDimensionsUtil.preferredMinWidthConstraint(item.itemBox());
      case ROW -> computeContentRow(item, grid);
      default -> throw new UnsupportedOperationException(
        "Unrecognize grid direction: " + direction);
    };
  }

  private static float maxContentRaw(
    GridItem item, Grid grid, GridDirection direction
  ) {
    return switch (direction) {
      case COLUMN -> EBDimensionsUtil.preferredWidthConstraint(item.itemBox());
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
      int i = item.rowLineStart();
      i < item.rowLineEnd();
      i++
    ) {
      LayoutConstraint size = grid.track(i, GridDirection.COLUMN).baseSize();
      assert size.isBounded();
      inlineSize += size.value();
    }

    UnmanagedBoxFragment<?> fragment = item.itemBox().layout(
      LayoutConstraint.of(inlineSize),
      LayoutConstraint.AUTO);
    return fragment.height(Measurement.CONTENT);
  }

}
