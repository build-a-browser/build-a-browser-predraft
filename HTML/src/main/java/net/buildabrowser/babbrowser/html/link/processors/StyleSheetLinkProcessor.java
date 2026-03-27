package net.buildabrowser.babbrowser.html.link.processors;

import java.nio.charset.StandardCharsets;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
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
    if (el.sheet() != null) {
      ((MutableDocument) el.nodeDocument()).styleSheets().removeStylesheet(el.sheet());
      el.setSheet(null);
      // TODO: Proper way to remove
    }

    if (success) {
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(
        new String(bodyBytes, StandardCharsets.UTF_8));
      CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
      // TODO: Set properties
      CSSStyleSheet styleSheet = CommonUtil.rethrow(() ->
        CSSParser.create().parseAStyleSheet(tokenizerStream));
      // TODO: Proper way to add
      ((MutableDocument) el.nodeDocument()).styleSheets().addStylesheet(styleSheet);
      el.setSheet(styleSheet);
    }

    // TODO: Fire event, unblock rendering
  }

}
