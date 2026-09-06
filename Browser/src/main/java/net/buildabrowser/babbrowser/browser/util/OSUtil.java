package net.buildabrowser.babbrowser.browser.util;

public final class OSUtil {
  
  private OSUtil() {}

  public static String getOSName() {
    String osName = System.getProperty("os.name", "").toLowerCase();
    String osVersion = System.getProperty("os.version", "");
    String rawArch = System.getProperty("os.arch", "").toLowerCase();

    String arch = rawArch.equals("amd64") ? "x86_64" : rawArch;
    if (osName.contains("linux")) {
      String sessionType = getLinuxDisplayServer();
      return sessionType + "; Linux " + arch;
    } else if (osName.contains("win")) {
      return getWindowsName(osVersion, arch);
    } else if (osName.contains("mac") || osName.contains("darwin")) {
      return getMacName(osVersion, arch);
    } else {
      return System.getProperty("os.name") + " " + osVersion + "; " + arch;
    }
  }

  private static String getMacName(String osVersion, String arch) {
    String versionFormatted = osVersion.replace('.', '_');
    String cpuType = (arch.contains("arm") || arch.contains("aarch64")) ?
      "Apple Mac OS X "  :
      "Intel Mac OS X ";
    return "Macintosh; " + cpuType + versionFormatted;
  }

  private static String getWindowsName(String osVersion, String arch) {
    String archToken =
      arch.contains("64") ? "Win64; x64" :
      System.getenv("PROCESSOR_ARCHITEW6432") != null ? "WOW64" :
      "Win32";
    return "Windows NT " + osVersion + "; " + archToken;
  }

  private static String getLinuxDisplayServer() {
    String xdgSession = System.getenv("XDG_SESSION_TYPE");
    String waylandDisplay = System.getenv("WAYLAND_DISPLAY");

    if (
      "wayland".equalsIgnoreCase(xdgSession)
      || (waylandDisplay != null && !waylandDisplay.isBlank())
    ) {
      return "Wayland";
    } else if (
      "x11".equalsIgnoreCase(xdgSession)
      || System.getenv("DISPLAY") != null
    ) {
      return "X11";
    } else {
      return "Unknown";
    }
  }

}
