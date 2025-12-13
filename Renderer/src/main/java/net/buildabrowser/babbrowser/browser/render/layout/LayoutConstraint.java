package net.buildabrowser.babbrowser.browser.render.layout;

public record LayoutConstraint(LayoutConstraintType type, int value) {
  
  public static final LayoutConstraint MIN_CONTENT = new LayoutConstraint(LayoutConstraintType.MIN_CONTENT, -1);
  public static final LayoutConstraint MAX_CONTENT = new LayoutConstraint(LayoutConstraintType.MAX_CONTENT, -1);
  public static final LayoutConstraint AUTO = new LayoutConstraint(LayoutConstraintType.AUTO, -1);

  public static LayoutConstraint of(int value) {
    return new LayoutConstraint(LayoutConstraintType.BOUNDED, value);
  }

  public boolean isPreLayoutConstraint() {
    return
      !type.equals(LayoutConstraintType.BOUNDED)
      && !type.equals(LayoutConstraintType.AUTO);
  }

  public static enum LayoutConstraintType {
    BOUNDED, AUTO, MIN_CONTENT, MAX_CONTENT
  }

}
