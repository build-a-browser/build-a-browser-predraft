package net.buildabrowser.babbrowser.cookies;

import java.util.List;

public interface CookieStore {

  PublicSuffixList publicSuffixList();
  
  // Expected to remove duplicate cookies
  Cookie storeValidatedCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  );

  List<Cookie> retrieveCookies(
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    SameSiteMode sameSite
  );

  List<Cookie> removeExpiredCookies();

  List<Cookie> removeExcessCookiesForHost(String host);

  List<Cookie> removeGlobalExcessCookies();

  // TODO: A more unified method would be nice
  boolean hasSecureCookie(
    String name,
    String host,
    String[] path
  );

}
