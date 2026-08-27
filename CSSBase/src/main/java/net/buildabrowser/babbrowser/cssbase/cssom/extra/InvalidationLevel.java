package net.buildabrowser.babbrowser.cssbase.cssom.extra;

public final class InvalidationLevel {
  
  private InvalidationLevel() {}

  public static final short NONE = 0;
  public static final short PAINT = 1;
  public static final short LAYOUT = 2;
  public static final short BOX = 4;
  public static final short STYLE = 8;
  public static final short STYLE_SELF = 16;

  public static final short ALL = 31;

}
