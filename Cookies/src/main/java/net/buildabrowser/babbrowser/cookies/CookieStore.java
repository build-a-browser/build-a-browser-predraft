package net.buildabrowser.babbrowser.cookies;

import java.util.List;

public interface CookieStore {

  PublicSuffixList publicSuffixList();
  
  void storeValidatedCookie(Cookie cookie);

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

}
