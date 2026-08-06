package net.buildabrowser.babbrowser.html.link.processors;

import java.nio.charset.StandardCharsets;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.html.LinkElement;

public class StyleSheetLinkProcessor extends DefaultLinkProcessor {

  @Override
  protected boolean linkedResourceFetchSetup(LinkElement el, FetchRequest request) {
    // TODO: Configure render blocking
    return true;
  }

  @Override
  protected void processLinkedResource(
    LinkElement el, boolean success, FetchResponse response, byte[] bodyBytes
  ) {
    // TODO: Sniff, check if still attached
    String contentType = response.headerList().get("Content-Type");
    int semiIndex = contentType == null ? -1 : contentType.indexOf(';');
    if (semiIndex != -1) {
      contentType = contentType.substring(0, semiIndex);
    }

    if (!(
      contentType == null
      || contentType.equals("text/css")
    )) {
      success = false;
    }

    if (el.sheet() != null) {
      el.nodeDocument().styleSheets().removeStylesheet(el.sheet());
      el.setSheet(null);
      // TODO: Proper way to remove
    }

    if (success) {
      CSSTokenStreamSource source = new CSSTokenStreamSource(
        response.url());
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(
        new String(bodyBytes, StandardCharsets.UTF_8));
      CSSTokenStream tokenizerStream = CSSTokenStream.create(source, tokenizerInput);
      // TODO: Set properties
      CSSStyleSheet styleSheet = CommonUtil.rethrow(() ->
        CSSParser.create().parseAStyleSheet(tokenizerStream));
      // TODO: Proper way to add
      el.nodeDocument().styleSheets().addStylesheet(styleSheet);
      el.setSheet(styleSheet);
    }

    // TODO: Fire event, unblock rendering
  }

}
