package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class GridTrackSizer {
  
  private GridTrackSizer() {}

  public static LayoutConstraint sizeFixed(
    LayoutContext layoutContext,
    LayoutConstraint reference,
    CSSValue dimension,
    boolean isAutoRepeat
  ) {
    LayoutConstraint size = SizingUtil.evaluateBaseSize(layoutContext, reference, dimension);
    if (!size.isBounded()) {
      size = LayoutConstraint.of(0);
    }
    if (isAutoRepeat && size.value() <= 0) {
      size = LayoutConstraint.of(1);
    }

    return size;
  }

}
