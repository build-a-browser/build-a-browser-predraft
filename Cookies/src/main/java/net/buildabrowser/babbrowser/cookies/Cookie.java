package net.buildabrowser.babbrowser.cookies;

import java.time.ZonedDateTime;

public record Cookie(
  String name,
  String value,
  boolean secure,
  String host,
  boolean hostOnly,
  String[] path,
  boolean hasPathAttribute,
  SameSite sameSite,
  boolean httpOnly,
  ZonedDateTime creationTime,
  ZonedDateTime expiryTime,
  ZonedDateTime lastAccessTime
) {

  public static String HOST_FAILURE = "<FAILURE>";
  
  public static enum SameSite {
    // Order matters for toInt/fromInt!
    STRICT, LAX, UNSET, NONE;

    public int toInt() {
      return ordinal();
    }

    public static SameSite fromInt(int value) {
      return SameSite.values()[value];
    }

  }

}
