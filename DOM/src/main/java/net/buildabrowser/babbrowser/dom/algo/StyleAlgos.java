package net.buildabrowser.babbrowser.dom.algo;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.Element;

public final class StyleAlgos {
  
  private StyleAlgos() {}

  // Added an extra baseURL param since HTMLDocument#baseURL cannot be called from here
  public static void updateAStyleBlock(Element styleElement, URI baseURL) {
    // TODO: The spec says to create the stylesheet ourselves?
    CSSTokenStreamSource source = new CSSTokenStreamSource(baseURL);
    CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(
      ElementAlgos.childTextContent(styleElement));
    CSSTokenStream tokenizerStream = CSSTokenStream.create(source, tokenizerInput);
    
    CSSStyleSheet styleSheet = CommonUtil.rethrow(() -> CSSParser.create().parseAStyleSheet(tokenizerStream));
    styleElement.nodeDocument().styleSheets().addStylesheet(styleSheet);
  }

}
