package net.buildabrowser.babbrowser.browser.util;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

  public static URI asDirectory(URI uri) {
      String path = uri.getPath();
      if (path.endsWith("/")) return uri;
      if (path.startsWith("/")) path = path.substring(1);
      return Paths.get(path + "/").toUri();
  }
  
  public static URI appConfigDirectory(String appName) {
    String osName = System.getProperty("os.name").toLowerCase();
    String homePath = System.getProperty("user.home");
    
    Path basePath =
      osName.contains("win") ? windowsPath(homePath) :
      osName.contains("mac") ? macPath(homePath) :
      unixPath(homePath);
    
    return basePath.resolve(appName + "/").toUri();
  }

  private static Path windowsPath(String homePath) {
    String appDataPath = System.getenv("APPDATA");
    return appDataPath == null ?
      Paths.get(homePath, "AppData", "Roaming") :
      Paths.get(appDataPath);
  }

  private static Path macPath(String homePath) {
    return Paths.get(homePath, "Library", "Application Support");
  }

  private static Path unixPath(String homePath) {
    String xdgConfigPath = System.getenv("XDG_CONFIG_HOME");
    return xdgConfigPath == null ?
      Paths.get(homePath, ".config") :
      Paths.get(xdgConfigPath);
  }


}
