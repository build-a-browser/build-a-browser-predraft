package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.cookies.CookieStore;

public record FetchConfig(
  FetchBackend backend,
  FetchPolicy policy,
  CookieStore cookieStore
) {
  
}
