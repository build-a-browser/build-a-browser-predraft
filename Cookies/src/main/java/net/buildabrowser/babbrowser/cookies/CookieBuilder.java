package net.buildabrowser.babbrowser.cookies;

import java.time.ZonedDateTime;

import net.buildabrowser.babbrowser.cookies.Cookie.SameSite;
import net.buildabrowser.babbrowser.cookies.imp.CookieBuilderImp;

public interface CookieBuilder {

  String name();

  String value();

  CookieBuilder setSecure(boolean secure);

  boolean secure();

  CookieBuilder setHost(String host);

  String host();

  void setHostOnly(boolean hostOnly);

  boolean hostOnly();

  CookieBuilder setPath(String[] path);

  String[] path();

  CookieBuilder setHasPathAttribute(boolean hasPathAttribute);

  boolean hasPathAttribute();

  CookieBuilder setSameSite(SameSite sameSite);

  SameSite sameSite();

  CookieBuilder setHttpOnly(boolean httpOnly);

  boolean httpOnly();

  CookieBuilder setCreationTime(ZonedDateTime creationTime);

  ZonedDateTime creationTime();

  CookieBuilder setExpiryTime(ZonedDateTime expiryTime);

  ZonedDateTime expiryTime();

  CookieBuilder setLastAccessTime(ZonedDateTime lastAccessTime);

  ZonedDateTime lastAccessTime();

  CookieBuilder setMaxAgeSeen(boolean maxAgeSeen);

  boolean maxAgeSeen();

  Cookie build();

  static CookieBuilder create(
    String name, String value
  ) {
    return new CookieBuilderImp(name, value);
  }

  static CookieBuilder fromCookie(Cookie cookie) {
    return create(cookie.name(), cookie.value())
      .setSecure(cookie.secure())
      .setHost(cookie.host())
      .setPath(cookie.path())
      .setHasPathAttribute(cookie.hasPathAttribute())
      .setSameSite(cookie.sameSite())
      .setHttpOnly(cookie.httpOnly())
      .setCreationTime(cookie.creationTime())
      .setExpiryTime(cookie.expiryTime())
      .setLastAccessTime(cookie.lastAccessTime());
  }
  
}
