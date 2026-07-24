package net.buildabrowser.babbrowser.cookies.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.Cookie.SameSite;

public class CookieParserUtilTest {
  
  @Test
  @DisplayName("Can parse simple name-value cookie")
  public void canParseSimpleNameValueCookie() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());

    Assertions.assertFalse(cookie.secure());
    Assertions.assertFalse(cookie.httpOnly());
    Assertions.assertEquals(SameSite.UNSET, cookie.sameSite());
  }

  @Test
  @DisplayName("Can parse cookie with just value")
  public void canParseCookieWithJustValue() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "VALUE", new String[] {""});
    Assertions.assertEquals("", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());
  }

  @Test
  @DisplayName("Can parse secure cookie")
  public void canParseSecureCookie() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE;secure", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());

    Assertions.assertTrue(cookie.secure());
    Assertions.assertFalse(cookie.httpOnly());
  }

  @Test
  @DisplayName("Can parse http-only cookie")
  public void canParseHttpOnlyCookie() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE;httponly", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());

    Assertions.assertFalse(cookie.secure());
    Assertions.assertTrue(cookie.httpOnly());
  }

  @Test
  @DisplayName("Can parse secure http-only cookie")
  public void canParseSecureHttpOnlyCookie() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE;secure;httponly", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());

    Assertions.assertTrue(cookie.secure());
    Assertions.assertTrue(cookie.httpOnly());
  }


  @Test
  @DisplayName("Can parse same-site cookie")
  public void canParseSameSiteCookie() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE;samesite=lax", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());
    Assertions.assertEquals(SameSite.LAX, cookie.sameSite());
  }

  @Test
  @DisplayName("Can parse double same-site cookie")
  public void canParseDoubleSameSiteCookie() {
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE;samesite=lax;samesite=STRICT", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());
    Assertions.assertEquals(SameSite.STRICT, cookie.sameSite());
  }

  @Test
  @DisplayName("Can parse expires cookie")
  public void canParseExpiresCookie() {
    // TODO: Problematically, this depends on the system clock not being too early
    Cookie cookie = CookieParserUtil.parseCookie(
      "NAME=VALUE;expires=Wed, 21 Oct 2026 07:28:00 GMT", new String[] {""});
    Assertions.assertEquals("NAME", cookie.name());
    Assertions.assertEquals("VALUE", cookie.value());
    ZonedDateTime expected = ZonedDateTime.of(
      2026, 10, 21, 7, 28, 0, 0, ZoneOffset.UTC);
    Assertions.assertEquals(expected, cookie.expiryTime());
  }

  // TODO: Test path, domain, and date parsing

}
