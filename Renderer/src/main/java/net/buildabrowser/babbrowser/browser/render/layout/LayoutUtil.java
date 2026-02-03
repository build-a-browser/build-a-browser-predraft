package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;

public final class LayoutUtil {
  
  private LayoutUtil() {}

  public static float constraintOrDim(LayoutConstraint constraint, float dim) {
    return constraint.type().equals(LayoutConstraintType.BOUNDED) ?
      constraint.value() : dim;
  }

}
