package net.buildabrowser.babbrowser.fetch;

public interface FetchPolicy {
  
  default boolean allowCookies(FetchRequest request) {
    return true;
  }

  default FetchResponse overrideResponse(FetchRequest request) {
    return null;
  }

}
