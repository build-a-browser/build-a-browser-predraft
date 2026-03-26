package net.buildabrowser.babbrowser.fetch;

public class FetchParameters {
  
  public FetchRequest request;

  public ProcessResponse processResponse;

  public ProcessResponseConsumeBody processResponseConsumeBody;

  public static interface ProcessResponse {
    void run(FetchResponse response);
  }

  public static interface ProcessResponseConsumeBody {
    void run(FetchResponse response, boolean success, byte[] bodyBytes);
  }

}
