package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import net.buildabrowser.babbrowser.common.util.Base64Util;
import net.buildabrowser.babbrowser.infra.StringUtil;

public final class DataURLProcessor {
  
  public static DataURL processDataURL(URI dataURL) {
    assert dataURL.getScheme().equals("data");
    String input = dataURL.toString(); // TODO: Properly serialize
    int[] position = new int[] { 5 };
    String mimeType = StringUtil.collectCodePoints(input, ch -> ch != ',', position);
    mimeType = mimeType.strip();
    position[0]++;
    String encodedBody = input.substring(position[0]);
    String stringBody = URLDecoder.decode(encodedBody.replace("+", "%2B"), StandardCharsets.UTF_8);
    byte[] body;
    if (mimeType.matches(".*;\\s*base64$")) {
      body = Base64Util.forgivingBase64Decode(stringBody);
      if (body == null) return null;
      mimeType = mimeType
        .substring(0, mimeType.length() - 6)
        .stripTrailing();
      mimeType = mimeType.substring(0, mimeType.length() - 1);
    } else {
      body = stringBody.getBytes();
    }

    if (mimeType.startsWith(";")) {
      mimeType = "text/plain" + mimeType;
    }

    // TODO: Parse mimeType
    return new DataURL(mimeType, body);
  }

  public static record DataURL(
    String mimeType,
    byte[] body
  ) {}

}
