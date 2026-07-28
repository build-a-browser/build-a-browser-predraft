package net.buildabrowser.cookies.stores.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.buildabrowser.babbrowser.common.util.URLUtil2;
import net.buildabrowser.babbrowser.cookies.Cookie;
import net.buildabrowser.babbrowser.cookies.Cookie.SameSite;
import net.buildabrowser.babbrowser.cookies.CookieBuilder;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.PublicSuffixList;
import net.buildabrowser.babbrowser.cookies.SameSiteMode;
import net.buildabrowser.babbrowser.cookies.exception.CookieStoreException;
import net.buildabrowser.babbrowser.cookies.stores.InMemoryCookieStore;
import net.buildabrowser.babbrowser.cookies.util.CookieUtil;

public class SQLiteCookieStore implements CookieStore {

  private final String jdbcURL;
  private final CookieStore inMemoryCookieStore;
  
  public SQLiteCookieStore(
    String jdbcURL,
    PublicSuffixList publicSuffixList
  ) {
    this.jdbcURL = jdbcURL;
    this.inMemoryCookieStore = new InMemoryCookieStore(publicSuffixList);
  }

  @Override
  public PublicSuffixList publicSuffixList() {
    return inMemoryCookieStore.publicSuffixList();
  }

  @Override
  public void initialize() throws CookieStoreException {
    JDBCUtil.execute(jdbcURL, CookieQueries.CREATE_COOKIES_TABLE_QUERY);
  }

  @Override
  public Cookie storeValidatedCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  ) throws CookieStoreException {
    String cookiePath = URLUtil2.serializePath(cookie.path());
    boolean isLargeCookie =
      cookie.name().length() > 255
      || cookie.host().length() > 255
      || cookiePath.length() > 255
      || cookie.value().length() > 255;
    if (
      cookie.expiryTime() != null
      && isLargeCookie
    ) return null; // Large cookies not supported

    Cookie adjustedCookie = removeDuplicateCookie(cookie, httpOnlyAllowed);
    if (adjustedCookie == null) return null;

    if (cookie.expiryTime() == null) {
      return inMemoryCookieStore.storeValidatedCookie(
        adjustedCookie, httpOnlyAllowed);
    }

    JDBCUtil.execute(jdbcURL, CookieQueries.CREATE_COOKIE_QUERY,
      cookie.name(),
      cookie.host(),
      cookiePath,
      cookie.value(),
      cookie.secure(),
      cookie.hostOnly(),
      cookie.httpOnly(),
      cookie.hasPathAttribute(),
      cookie.sameSite().toInt(),
      cookie.creationTime(),
      cookie.expiryTime(),
      cookie.lastAccessTime());
    return adjustedCookie;
  }

  @Override
  public List<Cookie> retrieveCookies(
    boolean isSecure,
    String host,
    String[] path,
    boolean httpOnlyAllowed,
    SameSiteMode sameSite
  ) throws CookieStoreException {
    List<Cookie> cookies = JDBCUtil
      .queryMany(jdbcURL, CookieQueries.RETRIEVE_COOKIES_QUERY, this::mapCookie, host)
      .stream()
      .filter(c -> CookieUtil.isCookieValidForHost(
        c, publicSuffixList(), isSecure, host, path,
        httpOnlyAllowed, sameSite))
      .collect(Collectors.toList());

    List<Cookie> temporaryCookies = inMemoryCookieStore.retrieveCookies(
      isSecure, host, path, httpOnlyAllowed, sameSite);

    ArrayList<Cookie> allCookies = new ArrayList<>(
      cookies.size() + temporaryCookies.size());
    allCookies.addAll(cookies);
    allCookies.addAll(temporaryCookies);
    return List.copyOf(allCookies);
  }

  @Override
  public List<Cookie> removeExpiredCookies() throws CookieStoreException {
    // TODO: Remove expired cookies
    return inMemoryCookieStore.removeExpiredCookies();
  }

  @Override
  public List<Cookie> removeExcessCookiesForHost(
    String host
  ) throws CookieStoreException {
    // TODO: Remove excess cookies for host
    return inMemoryCookieStore.removeExcessCookiesForHost(host);
  }

  @Override
  public List<Cookie> removeGlobalExcessCookies() throws CookieStoreException {
    // TODO: Remove global excess cookies
    return inMemoryCookieStore.removeGlobalExcessCookies();
  }

  @Override
  public boolean hasSecureCookie(
    String name, String host, String[] path
  ) throws CookieStoreException {
    List<Cookie> cookies = JDBCUtil.queryMany(
      jdbcURL, CookieQueries.RETRIEVE_SECURE_COOKIES_QUERY,
      this::mapCookie, host, host);
    
    for (Cookie existingCookie: cookies) {
      if (!(
        CookieUtil.isDomainMatch(existingCookie.host(), host)
        || CookieUtil.isDomainMatch(host, existingCookie.host())
      )) continue;
      if (!existingCookie.name().equals(name))
      if (!existingCookie.secure()) continue;
      if (
        !CookieUtil.pathMatches(path, existingCookie.path())
      ) continue;
      return true;
    }
    
    return inMemoryCookieStore.hasSecureCookie(name, host, path);
  }

  @Override
  public Cookie removeDuplicateCookie(
    Cookie cookie,
    boolean httpOnlyAllowed
  ) throws CookieStoreException {
    String cookiePath = URLUtil2.serializePath(cookie.path());
    Optional<Cookie> existingCookie = JDBCUtil.queryMaybe(
      jdbcURL, CookieQueries.RETRIEVE_DUPLICATE_COOKIE_QUERY, this::mapCookie,
      cookie.name(), cookie.host(), cookiePath, cookie.hostOnly());

    if (existingCookie.isEmpty()) {
      return inMemoryCookieStore.removeDuplicateCookie(cookie, httpOnlyAllowed);
    }

    Cookie oldCookie = existingCookie.get();
    boolean skipExisting = CookieUtil.skipExistingCookie(cookie, oldCookie, httpOnlyAllowed);
    if (skipExisting) return null;

    JDBCUtil.execute(
      jdbcURL, CookieQueries.REMOVE_DUPLICATE_COOKIE_QUERY,
      cookie.name(), cookie.host(), cookiePath, cookie.hostOnly());
    
    return CookieBuilder.fromCookie(cookie)
      .setCreationTime(oldCookie.creationTime())
      .build();
  }

  private Cookie mapCookie(
    ResultSet resultSet
  ) throws SQLException {
    return CookieBuilder.create(
      resultSet.getString("name"),
      resultSet.getString("value")
    )
      .setSecure(resultSet.getBoolean("secure"))
      .setHost(resultSet.getString("host"))
      .setHostOnly(resultSet.getBoolean("host_only"))
      .setPath(resultSet.getString("path").split("/"))
      .setHasPathAttribute(resultSet.getBoolean("has_path_attribute"))
      .setSameSite(SameSite.fromInt(resultSet.getInt("same_site")))
      .setHttpOnly(resultSet.getBoolean("http_only"))
      .setCreationTime(getDate(resultSet, "creation_time"))
      .setExpiryTime(getDate(resultSet, "expiry_time"))
      .setLastAccessTime(getDate(resultSet, "last_access_time"))
      .build();
  }

  private ZonedDateTime getDate(ResultSet resultSet, String columnName) throws SQLException {
    return OffsetDateTime.parse(
      resultSet.getString(columnName)
    ).atZoneSameInstant(ZoneId.of("GMT"));
  }
  
}
