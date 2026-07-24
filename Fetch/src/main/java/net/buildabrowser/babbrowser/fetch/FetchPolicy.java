package net.buildabrowser.babbrowser.fetch;

public interface FetchPolicy {
  
  boolean allowCookies(FetchRequest request);

}
