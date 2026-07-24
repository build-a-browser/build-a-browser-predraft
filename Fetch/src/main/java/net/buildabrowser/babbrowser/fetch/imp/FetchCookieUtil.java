package net.buildabrowser.babbrowser.fetch.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.SameSiteMode;
import net.buildabrowser.babbrowser.cookies.util.CookieUtil;
import net.buildabrowser.babbrowser.fetch.FetchConfig;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public final class FetchCookieUtil {
  
  private FetchCookieUtil() {}

  public static void appendRequestCookieHeader(
    FetchConfig fetchConfig,
    FetchRequest request
  ) {
    if (!fetchConfig.policy().allowCookies(request)) return;
    SameSiteMode sameSite = determineSameSiteMode(request);
    boolean isSecure = request.currentURL().getScheme().equals("https");
    boolean httpOnlyAllowed = true;
    List<Cookie> cookies = fetchConfig.cookieStore().retrieveCookies(
      isSecure, request.currentURL().getHost(),
      request.currentURL().getPath().split("/"), httpOnlyAllowed,
      sameSite);
    if (cookies.size() == 0) return;
    String value = CookieUtil.serializeCookies(cookies);
    request.headerList().append("Cookie", value);
  }

  public static void parseAndStoreResponseSetCookieHeaders(
    FetchConfig fetchConfig,
    FetchRequest request,
    MutableFetchResponse response
  ) {
    if (!fetchConfig.policy().allowCookies(request)) return;
    boolean allowNonHostOnlyCookieForPublicSuffix = false;
    boolean isSecure = request.currentURL().getScheme().equals("https");
    boolean httpOnlyAllowed = true;
    boolean sameSiteStrictOrLaxAllowed =
      determineSameSiteMode(request)
      .equals(SameSiteMode.STRICT_OR_LESS);
    response.headerList().forEach((name, value) -> {
      if (!name.equalsIgnoreCase("Set-Cookie")) return;
      CookieUtil.parseAndStoreCookie(
        fetchConfig.cookieStore(),
        value, isSecure, request.currentURL().getHost(),
        request.currentURL().getPath().split("/"), httpOnlyAllowed,
      allowNonHostOnlyCookieForPublicSuffix, sameSiteStrictOrLaxAllowed);
    });
    // Moved to outside of the loop for efficiency
    CookieUtil.garbageCollectCookies(
      fetchConfig.cookieStore(), request.currentURL().getHost());
  }

  private static SameSiteMode determineSameSiteMode(FetchRequest request) {
    assert request.method().equals("GET") || request.method().equals("POST");
    // TODO: (Security) Check various conditions
    return SameSiteMode.STRICT_OR_LESS;
  }

}
