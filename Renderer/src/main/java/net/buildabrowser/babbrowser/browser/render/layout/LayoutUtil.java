package net.buildabrowser.babbrowser.browser.render.layout;

public final class LayoutUtil {
  
  private LayoutUtil() {}

  public static float constraintOrDim(LayoutConstraint constraint, float dim) {
    return constraint.isBounded() ?
      constraint.value() : dim;
  }

}
