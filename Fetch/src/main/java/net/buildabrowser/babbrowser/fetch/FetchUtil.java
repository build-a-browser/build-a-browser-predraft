package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.fetch.FetchBody.FetchBodyWithType;

public final class FetchUtil {
  
  private FetchUtil() {}

  public static FetchBody getBytesAsABody(byte[] bytes) {
    return safelyExtractABodyWithType(bytes).body();
  }

  public static FetchBodyWithType safelyExtractABodyWithType(Object object) {
    // TODO: Assertion
    return extractABodyWithType(object, false);
  }

  public static FetchBodyWithType extractABodyWithType(Object object, boolean keepalive) {
    // TODO: Implement
    return null;
  }

}
