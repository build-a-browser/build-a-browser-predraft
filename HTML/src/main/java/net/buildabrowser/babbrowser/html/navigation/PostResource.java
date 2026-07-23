package net.buildabrowser.babbrowser.html.navigation;

import java.nio.ByteBuffer;

public record PostResource(
  ByteBuffer requestBody,
  String requestContentType
) {
  
  public boolean isFailure() {
    return requestBody != null;
  }

}
