package net.buildabrowser.babbrowser.common.util;

import java.net.URI;
import java.util.Objects;

public final class URLUtil2 {
  
  private URLUtil2() {}

  public static boolean equals(
    URI a, URI b, boolean excludeFragments
  ) {
    if (a == b) return true;
    if (a == null || b == null) return false;
    if (
      !excludeFragments
      && !a.getFragment().equals(b.getFragment())
    ) return false;

    return
      Objects.equals(a.getScheme(), b.getScheme())
      && Objects.equals(a.getAuthority(), b.getAuthority())
      && Objects.equals(a.getPath(), b.getPath())
      && Objects.equals(a.getQuery(), b.getQuery())
      && a.getPort() == b.getPort();
  }

}
