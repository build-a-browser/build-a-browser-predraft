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
    STRICT, LAX, UNSET, NONE;
  }

}
