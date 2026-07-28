package net.buildabrowser.babbrowser.cookies;

import java.util.List;

import net.buildabrowser.babbrowser.cookies.exception.CookieStoreException;

public interface CookieStore {

  PublicSuffixList publicSuffixList();
  
  // Expected to remove duplicate cookies
  Cookie storeValidatedCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  ) throws CookieStoreException;

  List<Cookie> retrieveCookies(
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    SameSiteMode sameSite
  ) throws CookieStoreException;

  List<Cookie> removeExpiredCookies() throws CookieStoreException;

  List<Cookie> removeExcessCookiesForHost(
    String host
  ) throws CookieStoreException;

  List<Cookie> removeGlobalExcessCookies() throws CookieStoreException;

  // TODO: A more unified method would be nice
  boolean hasSecureCookie(
    String name,
    String host,
    String[] path
  ) throws CookieStoreException;

  Cookie removeDuplicateCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  ) throws CookieStoreException;

  default void initialize() throws CookieStoreException {}

}
