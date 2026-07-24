package net.buildabrowser.babbrowser.cookies.imp;

import java.time.ZonedDateTime;

import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.Cookie.SameSite;
import net.buildabrowser.babbrowser.cookies.CookieBuilder;

public class CookieBuilderImp implements CookieBuilder {

  private final String name;
  private final String value;

  private boolean secure;
  private String host;
  private boolean hostOnly;
  private String[] path;
  private boolean hasPathAttribute;
  private SameSite sameSite = SameSite.UNSET;
  private boolean httpOnly;
  private boolean maxAgeSeen;

  private ZonedDateTime creationTime = ZonedDateTime.now();
  private ZonedDateTime expiryTime = null;
  private ZonedDateTime lastAccessTime = ZonedDateTime.now();

  public CookieBuilderImp(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String value() {
    return this.value;
  }

  @Override
  public CookieBuilder setSecure(boolean secure) {
    this.secure = secure;
    return this;
  }

  @Override
  public boolean secure() {
    return this.secure;
  }

  @Override
  public CookieBuilder setHost(String host) {
    this.host = host;
    return this;
  }

  @Override
  public String host() {
    return this.host;
  }

  @Override
  public void setHostOnly(boolean hostOnly) {
    this.hostOnly = hostOnly;
  }

  @Override
  public boolean hostOnly() {
    return this.hostOnly;
  }

  @Override
  public CookieBuilder setPath(String[] path) {
    this.path = path;
    return this;
  }

  @Override
  public String[] path() {
    return this.path;
  }

  @Override
  public CookieBuilder setHasPathAttribute(boolean hasPathAttribute) {
    this.hasPathAttribute = hasPathAttribute;
    return this;
  }

  @Override
  public boolean hasPathAttribute() {
    return this.hasPathAttribute;
  }

  @Override
  public CookieBuilder setSameSite(SameSite sameSite) {
    this.sameSite = sameSite;
    return this;
  }

  @Override
  public SameSite sameSite() {
    return this.sameSite;
  }

  @Override
  public CookieBuilder setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
    return this;
  }

  @Override
  public boolean httpOnly() {
    return this.httpOnly;
  }

  @Override
  public CookieBuilder setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
    return this;
  }

  @Override
  public ZonedDateTime creationTime() {
    return this.creationTime;
  }

  @Override
  public CookieBuilder setExpiryTime(ZonedDateTime expiryTime) {
    this.expiryTime = expiryTime;
    return this;
  }

  @Override
  public ZonedDateTime expiryTime() {
    return this.expiryTime;
  }

  @Override
  public CookieBuilder setLastAccessTime(ZonedDateTime lastAccessTime) {
    this.lastAccessTime = lastAccessTime;
    return this;
  }

  @Override
  public ZonedDateTime lastAccessTime() {
    return this.lastAccessTime;
  }

  @Override
  public CookieBuilder setMaxAgeSeen(boolean maxAgeSeen) {
    this.maxAgeSeen = maxAgeSeen;
    return this;
  }

  @Override
  public boolean maxAgeSeen() {
    return this.maxAgeSeen;
  }

  @Override
  public Cookie build() {
    return new Cookie(
      name, value, secure, host, hostOnly,
      path, hasPathAttribute, sameSite, httpOnly,
      creationTime, expiryTime, lastAccessTime);
  }
  
}
