package net.buildabrowser.babbrowser.network;

public final class ExtensionUtil {
  
  private ExtensionUtil() {}

  public static String guessMimeTypeFromFileName(String filename) {
    String extension = getExtension(filename).toLowerCase();
    return switch (extension) {
      case "html", "htm" -> "text/html";
      case "css" -> "text/css";
      case "png" -> "image/png";
      case "jpg", "jpeg" -> "image/jpeg";
      case "gif" -> "image/gif";
      case "txt" -> "text/plain";
      case "js" -> "application/javascript";
      case "json" -> "application/json";
      default -> null;
    };
  }

  private static String getExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1);
  }

}
