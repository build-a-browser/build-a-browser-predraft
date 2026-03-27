package net.buildabrowser.babbrowser.dom.algo;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public final class StyleAlgos {
  
  private StyleAlgos() {}

  public static void updateAStyleBlock(MutableElement styleElement) {
    // TODO: The spec says to create the stylesheet ourselves?
    CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(
      ElementAlgos.childTextContent(styleElement));
    CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
    
    CSSStyleSheet styleSheet = CommonUtil.rethrow(() -> CSSParser.create().parseAStyleSheet(tokenizerStream));
    styleElement.nodeDocument().styleSheets().addStylesheet(styleSheet);
  }

}
