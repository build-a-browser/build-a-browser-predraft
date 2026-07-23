package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public class FetchParameters {
  
  public MutableFetchRequest request;

  public ProcessResponse processResponse;

  public ProcessResponseConsumeBody processResponseConsumeBody;

  public static interface ProcessResponse {
    void run(FetchResponse response);
  }

  public static interface ProcessResponseConsumeBody {
    void run(FetchResponse response, boolean success, byte[] bodyBytes);
  }

}
