package net.buildabrowser.babbrowser.html.form;

import java.util.List;

import net.buildabrowser.babbrowser.html.form.XWWWFormURLEncodedSerializer.NameValuePair;

public final class TextPlainEncodedSerializer {
  
  private TextPlainEncodedSerializer() {}

  public static String serialize(
    List<NameValuePair> pairs
  ) {
    StringBuilder result = new StringBuilder();
    for (NameValuePair pair: pairs) {
      result
        .append(pair.name())
        .append('=')
        .append(pair.value())
        .append("\r\n");
    }

    return result.toString();
  }

}
