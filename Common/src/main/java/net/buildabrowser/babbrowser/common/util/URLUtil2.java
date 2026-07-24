package net.buildabrowser.babbrowser.common.util;

import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public final class URLUtil2 {

  // The IPv4 regex is from Baeldung
  private static final Pattern IPV4_REGEX
    = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");
  // The IPv6 regex is AI-generated
  private static final Pattern IPV6_REGEX
    = Pattern.compile(
      "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|" +
      "([0-9a-fA-F]{1,4}:){1,7}:|" +
      "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
      "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
      "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
      "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
      "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
      "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
      ":((:[0-9a-fA-F]{1,4}){1,7}|:))$");
  
  
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

  public static boolean isIPv4(String host) {
    return IPV4_REGEX.matcher(host).matches();
  }

  public static boolean isIPv6(String host) {
    return IPV6_REGEX.matcher(host).matches();
  }

  // TODO: Check the correct way to serialize a path
  public static String serializePath(String[] path) {
    StringJoiner joiner = new StringJoiner("/");
    for (String part: path) {
      joiner.add(part);
    }
    return joiner.toString();
  }

}
