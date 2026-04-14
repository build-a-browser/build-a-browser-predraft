package net.buildabrowser.babbrowser.fetch;

public final class FetchUtil {
  
  private FetchUtil() {}

  public static boolean isFetchScheme(String scheme) {
    return switch (scheme) {
      case "about" -> true;
      case "blob" -> true;
      case "data" -> true;
      case "file" -> true;
      case "http" -> true;
      case "https" -> true;
      default -> false;
    };
  }

}
