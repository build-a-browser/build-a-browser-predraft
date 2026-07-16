package net.buildabrowser.babbrowser.html.form;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class XWWWFormURLEncodedSerializer {
  
  private XWWWFormURLEncodedSerializer() {}

  public static String serialize(
    List<NameValuePair> tuples,
    Charset encoding
  ) {
    if (encoding == null) {
      encoding = StandardCharsets.UTF_8;
    }

    StringBuilder output = new StringBuilder();
    for (NameValuePair tuple: tuples) {
      // TODO: Assert scalar value
      String name = percentEncodeAfterEncoding(tuple.name(), encoding);
      String value = percentEncodeAfterEncoding(tuple.value(), encoding);
      if (output.length() != 0) {
        output.append('&');
      }
      output
        .append(name)
        .append('=')
        .append(value);
    }
    return output.toString();
  }

  public static record NameValuePair(
    String name, String value
  ) {}


  // TODO: Proper way to encode
  private static String percentEncodeAfterEncoding(
    String value, Charset encoding
  ) {
    return URLEncoder.encode(value, encoding);
  }

}
