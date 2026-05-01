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

  public static boolean isHTTPScheme(String scheme) {
    return scheme.equals("http") || scheme.equals("https");
  }

  public static boolean isRedirectStatus(int status) {
    return switch (status) {
      case 301, 302, 303, 307, 308 -> true;
      default -> false;
    };
  }

}
