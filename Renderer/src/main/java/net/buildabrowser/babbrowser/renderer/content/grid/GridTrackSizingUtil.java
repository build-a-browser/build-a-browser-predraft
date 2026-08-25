package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue.FitContent;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class GridTrackSizingUtil {
  
  private GridTrackSizingUtil() {}

  public static boolean isIntrinsicSizingFunction(
    CSSValue sizingFunction
  ) {
    return
      sizingFunction.equals(CSSValue.AUTO)
      || sizingFunction instanceof SizeValue
      || sizingFunction instanceof FitContent;
  }

  public static boolean isFlexibleSizingFunction(
    CSSValue sizingFunction
  ) {
    return
      sizingFunction instanceof LengthValue lengthValue
      && LengthType.FR.equals(lengthValue.dimension());
  }

  public static LayoutConstraint evaluateFixedSize(
    Grid grid,
    LayoutConstraint parentConstraint,
    CSSValue sizingFunction
  ) {
    // TODO: Better to use the layoutContext passed to content?
    LayoutContext layoutContext = grid.gridBox().layoutContext();
    return SizingUtil.evaluateBaseSize(
      layoutContext, parentConstraint, sizingFunction);
  }

  public static float unusedSpace(
    Grid grid,
    LayoutConstraint parentConstraint,
    GridDirection direction
  ) {
    if (!parentConstraint.isBounded()) return 0;
    float unusedSpace = parentConstraint.value();
    for (GridTrack track: grid.tracks(direction)) {
      assert track.baseSize().isBounded();
      unusedSpace -= track.baseSize().value();
    }

    return Math.max(0, unusedSpace);
  }

}
