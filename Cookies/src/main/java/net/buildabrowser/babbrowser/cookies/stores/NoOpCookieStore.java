package net.buildabrowser.babbrowser.cookies.stores;

import java.util.List;

import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.PublicSuffixList;
import net.buildabrowser.babbrowser.cookies.SameSiteMode;

public class NoOpCookieStore implements CookieStore {

  private final PublicSuffixList suffixList;

  public NoOpCookieStore(PublicSuffixList suffixList) {
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
    return cookie;
  }

  @Override
  public List<Cookie> retrieveCookies(
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    SameSiteMode sameSite
  ) {
    return List.of();
  }

  @Override
  public List<Cookie> removeExpiredCookies() {
    return List.of();
  }

  @Override
  public List<Cookie> removeExcessCookiesForHost(String host) {
    return List.of();
  }

  @Override
  public List<Cookie> removeGlobalExcessCookies() {
    return List.of();
  }

  @Override
  public boolean hasSecureCookie(
    String name,
    String host,
    String[] path
  ) {
    return false;
  }
  
}
