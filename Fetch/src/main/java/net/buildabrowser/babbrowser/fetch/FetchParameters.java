package net.buildabrowser.babbrowser.fetch;

public class FetchParameters {
  
  public FetchRequest request;

  public ProcessResponseConsumeBody processResponseConsumeBody;

  public static interface ProcessResponseConsumeBody {
    void run(FetchResponse response, boolean success, byte[] bodyBytes);
  }

}
