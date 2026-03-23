package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.stream.ReadableStream;

public record FetchBody(
  ReadableStream stream, Object source, Integer length
) {
  
  public static record FetchBodyWithType(
    FetchBody body, String type
  ) {}

}
