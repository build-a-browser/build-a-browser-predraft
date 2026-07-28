package net.buildabrowser.babbrowser.browser;

public final class BrowserVersion {
  
  public static String NAME = "BuildABrowser Browser";
  public static int MAJOR_VERSION = 0;
  public static int MINOR_VERSION = 1;
  public static int PATCH_VERSION = 0;

  private BrowserVersion() {}

  public static String asVersionString() {
    return new StringBuilder(NAME)
      .append(" v")
      .append(MAJOR_VERSION)
      .append('.')
      .append(MINOR_VERSION)
      .append('.')
      .append(PATCH_VERSION)
      .toString();
  }

}
