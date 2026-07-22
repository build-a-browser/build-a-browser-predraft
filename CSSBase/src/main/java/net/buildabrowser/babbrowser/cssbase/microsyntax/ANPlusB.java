package net.buildabrowser.babbrowser.cssbase.microsyntax;

public record ANPlusB(
  int a, int b
) {
  
  public static final ANPlusB ODD = new ANPlusB(2, 1);
  public static final ANPlusB EVEN = new ANPlusB(2, 0);

  public static ANPlusB create(int a, int b) {
    return new ANPlusB(a, b);
  }

}
