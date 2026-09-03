package net.buildabrowser.babbrowser.cookies.stores;

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

  // TODO: Could also be a Map<String, Map<String, List<Cookie>>> to store
  // the name as a key, need to check if it's worth it
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
  public Cookie storeValidatedCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  ) {
    synchronized (cookieMap) {
      Cookie adjustedCookie = removeDuplicateCookie(
        cookie, httpOnlyAllowed);
      if (adjustedCookie == null) return null;

      if (
        cookie.expiryTime() != null
        && cookie.expiryTime().isBefore(ZonedDateTime.now())
      ) return null;

      cookieMap
        .computeIfAbsent(cookie.host(), _1 -> new ArrayList<>())
        .add(adjustedCookie);

      return adjustedCookie;
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
              .setLastAccessTime(ZonedDateTime.now())
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

  @Override
  public boolean hasSecureCookie(
    String name,
    String host,
    String[] path
  ) {
    synchronized (cookieMap) {
      for (Map.Entry<String, List<Cookie>> entry: cookieMap.entrySet()) {
        String mapHost = entry.getKey();
        List<Cookie> cookieList = entry.getValue();
        if (!(
          CookieUtil.isDomainMatch(mapHost, host)
          || CookieUtil.isDomainMatch(host, mapHost)
        )) continue;
        for (Cookie existingCookie: cookieList) {
          if (!existingCookie.name().equals(name))
          if (!existingCookie.secure()) continue;
          if (
            !CookieUtil.pathMatches(path, existingCookie.path())
          ) continue;
          return true;
        }
      };
    }

    return false;
  }

  @Override
  public Cookie removeDuplicateCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  ) {
    // cookieMap is expected to already be synchronized
    for (Map.Entry<String, List<Cookie>> entry: cookieMap.entrySet()) {
      String mapHost = entry.getKey();
      if (!CookieUtil.hostEquals(mapHost, cookie.host())) continue;
      List<Cookie> cookieList = entry.getValue();
      ListIterator<Cookie> cookieIt = cookieList.listIterator();
      while (cookieIt.hasNext()) {
        Cookie oldCookie = cookieIt.next();
        if (!oldCookie.name().equals(cookie.name())) continue;
        if (oldCookie.hostOnly() != cookie.hostOnly()) continue;
        if (!CookieUtil.pathEquals(oldCookie.path(), cookie.path())) continue;

        if (
          !httpOnlyAllowed
          && oldCookie.httpOnly()
        ) return null;

        boolean changed = CookieUtil.isCookieChanged(cookie, oldCookie, httpOnlyAllowed);
        if (!changed) {
          cookieIt.set(cookie);
          return null;
        }

        cookieIt.remove();
        return CookieBuilder.fromCookie(cookie)
          .setCreationTime(oldCookie.creationTime())
          .build();
      }
    }

    return cookie;
  }
  
}
