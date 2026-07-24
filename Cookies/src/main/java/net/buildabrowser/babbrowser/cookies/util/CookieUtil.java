package net.buildabrowser.babbrowser.cookies.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.common.util.URLUtil2;
import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.Cookie.SameSite;
import net.buildabrowser.babbrowser.cookies.CookieBuilder;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.PublicSuffixList;
import net.buildabrowser.babbrowser.cookies.SameSiteMode;

public final class CookieUtil {
  
  private CookieUtil() {}

  public static void parseAndStoreCookie(
    CookieStore cookieStore,
    String value,
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    boolean allowNonHostOnlyCookieForPublicSuffix,
    boolean sameSiteStrictOrLaxAllowed
  ) {
    Cookie cookie = CookieParserUtil.parseCookie(value, path);
    storeCookie(
      cookieStore, cookie, isSecure, host, httpOnlyAllowed,
      allowNonHostOnlyCookieForPublicSuffix, sameSiteStrictOrLaxAllowed);
  }

  public static String serializeCookies(List<Cookie> cookies) {
    StringBuilder output = new StringBuilder();
    for (Cookie cookie: cookies) {
      if (output.length() > 0) {
        output.append("; ");
      }
      if (cookie.name().length() > 0) {
        output.append(cookie.name());
        output.append('=');
      }
      output.append(cookie.value());
    }

    return output.toString();
  }

  public static Cookie storeCookie(
    CookieStore cookieStore,
    Cookie cookie_,
    boolean isSecure,
    String host,
    boolean httpOnlyAllowed,
    boolean allowNonHostOnlyCookieForPublicSuffix,
    boolean sameSiteStrictOrLaxAllowed
  ) {
    CookieBuilder cookie = CookieBuilder.fromCookie(cookie_);

    int cookieLength = cookie.name().length() + cookie.value().length();
    assert cookieLength != 0 && cookieLength <= 4096;
    // TODO: Assert character range
    if (cookie.host() == Cookie.HOST_FAILURE) return null;
    ZonedDateTime currentTime = ZonedDateTime.now();
    cookie.setCreationTime(currentTime);
    cookie.setLastAccessTime(currentTime);
    if (
      !allowNonHostOnlyCookieForPublicSuffix
      && cookieStore.publicSuffixList().contains(cookie.host())
    ) {
      // TODO: proper host-equal
      if (cookie.host().equals(host)) {
        cookie.setHost(null);
      } else {
        return null;
      }
    }

    if (cookie.host() == null) {
      cookie.setHostOnly(true);
      cookie.setHost(host);
    } else {
      if (
        !isDomainMatch(host, cookie.host())
      ) return null;
      cookie.setHostOnly(false);
    }

    assert cookie.host() != null && cookie.host() != Cookie.HOST_FAILURE;

    if (
      !httpOnlyAllowed
      && cookie.httpOnly()
    ) return null;

    if (!isSecure) {
      if (cookie.secure()) return null;
      // TODO: Check if a secure variant already exists
    }

    if (
      !cookie.sameSite().equals(SameSite.NONE)
      && !sameSiteStrictOrLaxAllowed
    ) return null;

    if (
      cookie.sameSite().equals(SameSite.NONE)
      && !cookie.secure()
    ) return null;

    if (
      isCookiePrefixNotValidInContext(cookie)
    ) return null;

    // TODO: Check existing cookie
    Cookie finalCookie = cookie.build();
    cookieStore.storeValidatedCookie(finalCookie);
    return finalCookie;
  }

  public static List<Cookie> garbageCollectCookies(
    CookieStore cookieStore, String host
  ) {
    List<Cookie> expiredCookies = cookieStore.removeExpiredCookies();
    List<Cookie> excessHostCookies = cookieStore.removeExcessCookiesForHost(host);
    List<Cookie> excessGlobalCookies = cookieStore.removeGlobalExcessCookies();
    List<Cookie> removedCookies = new ArrayList<>(
      expiredCookies.size()
      + excessHostCookies.size()
      + excessGlobalCookies.size());
    removedCookies.addAll(expiredCookies);
    removedCookies.addAll(excessHostCookies);
    removedCookies.addAll(excessGlobalCookies);

    return removedCookies;
  }

  public static boolean isDomainMatch(
    String host, String domainAttributeValue
  ) {
    return
      host.equals(domainAttributeValue)
      || (
        isDomain(host)
        && host.endsWith("." + domainAttributeValue));
  }

  public static boolean isCookieValidForHost(
    Cookie cookie,
    PublicSuffixList suffixList,
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    SameSiteMode sameSite
  ) {
    boolean isCorrectHost =
      (cookie.hostOnly() && host.equals(cookie.host())) // TODO: Check host-equal
      || (!cookie.hostOnly() && CookieUtil.isDomainMatch(host, cookie.host()));
    if (!isCorrectHost) return false;
    if (!cookie.hostOnly() && suffixList.contains(cookie.host())) return false;
    if (!CookieUtil.patchMatches(path, cookie.path())) return false;
    if (isSecure && !cookie.secure()) return false;
    if (cookie.httpOnly() && !httpOnlyAllowed) return false;

    boolean allowStrict = sameSite.equals(SameSiteMode.STRICT_OR_LESS);
    boolean allowLax = sameSite.equals(SameSiteMode.LAX_OR_LESS);
    boolean allowUnset = sameSite.equals(SameSiteMode.UNSET_OR_LESS);
    boolean isAllowed = switch (cookie.sameSite()) {
      case STRICT -> allowStrict;
      case LAX -> allowStrict || allowLax;
      case UNSET -> allowStrict || allowLax || allowUnset;
      case NONE -> true;
      default -> throw new UnsupportedOperationException(
        "Unrecognized sameSite: " + cookie.sameSite());
    };

    return isAllowed;
  }

  public static boolean patchMatches(
    String[] requestPath,
    String[] cookiePath
  ) {
    String serializedRequestPath = URLUtil2.serializePath(requestPath);
    String serializedCookiePath = URLUtil2.serializePath(cookiePath);
    if (
      serializedCookiePath.equals(serializedRequestPath)
    ) return true;
    if (
      serializedRequestPath.startsWith(serializedCookiePath)
      && serializedCookiePath.endsWith("/")
    ) return true;
    return (serializedRequestPath + "/")  
      .startsWith(serializedCookiePath);
  }

  public static int compareCookies(Cookie a, Cookie b) {
    return
      a.path().length < b.path().length ? 1 :
      a.path().length > b.path().length ? -1 :
      a.creationTime().isBefore(b.creationTime()) ? -1 :
      a.creationTime().isAfter(b.creationTime()) ? 1 :
      0;
  }

  private static boolean isDomain(String host) {
    return
      !URLUtil2.isIPv4(host)
      && !URLUtil2.isIPv6(host);
  }

  private static boolean isCookiePrefixNotValidInContext(CookieBuilder cookie) {
    String lowerName = cookie.name().toLowerCase();
    if (
      lowerName.startsWith("__secure-")
      && !cookie.secure()
    ) return true;
    if (
      lowerName.startsWith("__host-")
      && !isHostPrefixCompatible(cookie)
    ) return true;
    if (
      lowerName.startsWith("__http-")
      && !isHttpPrefixCompatible(cookie)
    ) return true;
    if (
      lowerName.startsWith("__host-http-")
      && !(
        isHostPrefixCompatible(cookie)
        && isHttpPrefixCompatible(cookie))
    ) return true;

    String lowerValue = cookie.value().toLowerCase();
    if (
      cookie.name().length() == 0
      && (
        lowerValue.startsWith("__secure-")
        || lowerValue.startsWith("__host-")
        || lowerValue.startsWith("__http-")
        // The below case is technically already covered by case 2
        || lowerValue.startsWith("__host-http-")
      )
    ) return true;

    return false;
  }

  private static boolean isHostPrefixCompatible(
    CookieBuilder cookie
  ) {
    return
      cookie.secure()
      && cookie.hostOnly()
      && cookie.hasPathAttribute()
      && cookie.path().length == 1
      && cookie.path()[0].length() == 0;
  }

  private static boolean isHttpPrefixCompatible(
    CookieBuilder cookie
  ) {
    return
      cookie.secure()
      && !cookie.httpOnly();
  }

}
