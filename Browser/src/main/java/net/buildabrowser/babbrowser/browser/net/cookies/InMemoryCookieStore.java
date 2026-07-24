package net.buildabrowser.babbrowser.browser.net.cookies;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.CookieBuilder;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.PublicSuffixList;
import net.buildabrowser.babbrowser.cookies.SameSiteMode;
import net.buildabrowser.babbrowser.cookies.util.CookieUtil;

public class InMemoryCookieStore implements CookieStore {

  private final Map<String, List<Cookie>> cookieMap = new HashMap<>();

  private final PublicSuffixList suffixList;

  public InMemoryCookieStore(PublicSuffixList suffixList) {
    this.suffixList = suffixList;
  }

  @Override
  public PublicSuffixList publicSuffixList() {
    return this.suffixList;
  }

  @Override
  public void storeValidatedCookie(Cookie cookie) {
    synchronized (cookieMap) {
      cookieMap
        .computeIfAbsent(cookie.host(), _ -> new ArrayList<>())
        .add(cookie);
    }
  }

  @Override
  public List<Cookie> retrieveCookies(
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    SameSiteMode sameSite
  ) {
    List<Cookie> cookies = new ArrayList<>();

    synchronized (cookieMap) {
      cookieMap.forEach((mapHost, cookieList) -> {
        if (!CookieUtil.isDomainMatch(mapHost, host)) return;
        ListIterator<Cookie> cookieIt = cookieList.listIterator();
        while (cookieIt.hasNext()) {
          Cookie cookie = cookieIt.next();
          boolean isMatch = CookieUtil.isCookieValidForHost(
            cookie, suffixList, isSecure, host, path, httpOnlyAllowed, sameSite);
          if (isMatch) {
            Cookie adjustedCookie = CookieBuilder
              .fromCookie(cookie)
              .setCreationTime(ZonedDateTime.now())
              .build();
            cookieIt.set(adjustedCookie);
            cookies.add(adjustedCookie);
          }
        }
      });
    }

    cookies.sort(CookieUtil::compareCookies);

    return cookies;
  }

  @Override
  public List<Cookie> removeExpiredCookies() {
    // TODO: Remove expired cookies
    return List.of();
  }

  @Override
  public List<Cookie> removeExcessCookiesForHost(String host) {
    // TODO: Remove excess cookies for host
    return List.of();
  }

  @Override
  public List<Cookie> removeGlobalExcessCookies() {
    // TODO: Remove global excess cookies
    return List.of();
  }
  
}
