package net.buildabrowser.babbrowser.cookies.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.common.util.NumberUtil;
import net.buildabrowser.babbrowser.common.util.StringUtil;
import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.Cookie.SameSite;
import net.buildabrowser.babbrowser.cookies.CookieBuilder;
import net.buildabrowser.babbrowser.cookies.CookieLimits;

public final class CookieParserUtil {

  private static final int DAY_AS_SECONDS = 24 * 60 * 60;
  
  private CookieParserUtil() {}

  // The spec processes as a byte array, I'm using chars, hopefully
  // this properly handles non-ascii values still...
  public static Cookie parseCookie(
    // Omitted a few parameters that the spec mentioned but did not use
    String input, String[] path
  ) {
    for (int i = 0; i < input.length(); i++) {
      char ch = input.charAt(i);
      if (
        (ch >= 0x00 && ch <= 0x08)
        || (ch >= 0x0A && ch <= 0x1F)
        || ch == 0x7F
      ) return null;
    }

    String nameValueInput = input;
    String attributesInput = "";
    int semicolonIndex = input.indexOf(';');
    if (semicolonIndex != -1) {
      nameValueInput = input.substring(0, semicolonIndex);
      attributesInput = input.substring(semicolonIndex);
    }

    String name = "";
    String value = nameValueInput;
    int eqIndex = nameValueInput.indexOf('=');
    if (eqIndex != -1) {
      name = nameValueInput.substring(0, eqIndex);
      value = nameValueInput.substring(eqIndex + 1);
    }

    name = StringUtil.stripWhitespace(name);
    value = StringUtil.stripWhitespace(value);

    CookieBuilder cookie = CookieBuilder.create(name, value);
    cookie.setPath(cookieDefaultPath(path));

    int attributesInputIndex = 0;
    while (attributesInputIndex < attributesInput.length()) {
      char ch = attributesInput.charAt(attributesInputIndex++);
      assert ch == ';';
      String attributeNameValueInput = null;
      semicolonIndex = attributesInput.indexOf(';', attributesInputIndex);
      if (semicolonIndex != -1) {
        attributeNameValueInput = attributesInput.substring(
          attributesInputIndex, semicolonIndex);
        attributesInputIndex = semicolonIndex;
      } else {
        attributeNameValueInput = attributesInput.substring(attributesInputIndex);
        attributesInputIndex = attributesInput.length();
      }

      String attributeName = attributeNameValueInput;
      String attributeValue = "";
      eqIndex = attributeNameValueInput.indexOf('=');
      if (eqIndex != -1) {
        attributeName = attributeNameValueInput.substring(0, eqIndex);
        attributeValue = attributeNameValueInput.substring(eqIndex + 1);
      }

      attributeName = StringUtil.stripWhitespace(attributeName);
      attributeValue = StringUtil.stripWhitespace(attributeValue);
      if (attributeValue.length() > 1024) continue;

      switch (attributeName.toLowerCase()) {
        case "expires" -> parseCookieExpires(cookie, attributeValue);
        case "max-age" -> parseCookieMaxAge(cookie, attributeValue);
        case "domain" -> parseCookieDomain(cookie, attributeValue);
        case "path" -> parseCookiePath(cookie, attributeValue);
        case "secure" -> cookie.setSecure(true);
        case "httponly" -> cookie.setHttpOnly(true);
        case "samesite" -> parseCookieSameSite(cookie, attributeValue);
        default -> {}
      }
    }

    return cookie.build();
  }

  private static void parseCookieExpires(
    CookieBuilder cookie, String attributeValue
  ) {
    if (cookie.maxAgeSeen()) return;
    ZonedDateTime expiryTime = parseDate(attributeValue);
    // Spec says attributeValue, but I believe it meant to say expiryTime
    if (expiryTime == null) return;
    ZonedDateTime maxTime = ZonedDateTime.now().plusSeconds(
      CookieLimits.COOKIE_AGE_LIMIT * DAY_AS_SECONDS);
    if (expiryTime.isAfter(maxTime)) {
      expiryTime = maxTime;
    }
    cookie.setExpiryTime(expiryTime);
  }

  private static void parseCookieMaxAge(
    CookieBuilder cookie, String attributeValue
  ) {
    if (attributeValue.length() == 0) return;
    Integer deltaSeconds = NumberUtil.parseInteger(attributeValue);
    if (deltaSeconds == null) return;
    deltaSeconds = Math.min(
      deltaSeconds,
      CookieLimits.COOKIE_AGE_LIMIT * DAY_AS_SECONDS);
    ZonedDateTime expiryTime = deltaSeconds <= 0 ?
      ZonedDateTime.of(LocalDate.MIN, LocalTime.MIN, ZoneOffset.UTC) :
      ZonedDateTime.now().plusSeconds(deltaSeconds);
    cookie.setExpiryTime(expiryTime);
    cookie.setMaxAgeSeen(true);
  }

  private static void parseCookieDomain(
    CookieBuilder cookie, String attributeValue
  ) {
    String host = Cookie.HOST_FAILURE;
    boolean isOnlyASCII = true;
    for (int i = 0; i < attributeValue.length(); i++) {
      isOnlyASCII &= attributeValue.charAt(i) < 0x80;
    }
    if (isOnlyASCII) {
      String hostInput = attributeValue;
      if (hostInput.charAt(0) == '.') {
        hostInput = hostInput.substring(1);
        host = hostInput.toLowerCase(); // TODO: Parse host
      } else {
        host = hostInput.toLowerCase();
      }
    }

    cookie.setHost(host);
  }

  private static void parseCookiePath(CookieBuilder cookie, String attributeValue) {
    if (attributeValue.length() > 0 && attributeValue.charAt(0) == '/') {
      if (attributeValue.equals("/")) {
        cookie.setPath(new String[] { "" });
      } else {
        cookie.setPath(attributeValue.split("/"));
      }
      cookie.setHasPathAttribute(true);
    }
  }

  private static void parseCookieSameSite(
    CookieBuilder cookie, String attributeValue
  ) {
    switch (attributeValue.toLowerCase()) {
      case "none" -> cookie.setSameSite(SameSite.NONE);
      case "strict" -> cookie.setSameSite(SameSite.STRICT);
      case "lax" -> cookie.setSameSite(SameSite.LAX);
      default -> {}
    }
  }

  private static ZonedDateTime parseDate(String attributeValue) {
    // TODO: Proper way to parse a date
    
    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
    ZonedDateTime result = CommonUtil.tryOrNull(
      () -> ZonedDateTime.parse(attributeValue, formatter));
    if (result != null) return result;

    return CommonUtil.tryOrNull(
      () -> ZonedDateTime.parse(
        attributeValue
          .replaceFirst("-", " ")
          .replaceFirst("-", " "),
        formatter));
  }

  private static String[] cookieDefaultPath(String[] path) {
    assert path.length > 0;
    if (path.length > 1) {
      String[] newPath = new String[path.length - 1];
      System.arraycopy(path, 0, newPath, 0, newPath.length);
      return newPath;
    } else {
      return new String[] { "" };
    }
  }

}
