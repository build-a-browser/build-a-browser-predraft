package net.buildabrowser.babbrowser.dom.algo;

import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.parser.CSSParser;
import net.buildabrowser.babbrowser.css.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.utils.CommonUtils;

public final class StyleAlgos {
  
  private StyleAlgos() {}

  public static void updateAStyleBlock(Element styleElement, MutableDocument document) {
    // TODO: The spec says to create the stylesheet ourselves?
    CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(
      ElementAlgos.childTextContent(styleElement));
    CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
    
    CSSStyleSheet styleSheet = CommonUtils.rethrow(() -> CSSParser.create().parseAStyleSheet(tokenizerStream));
    document.styleSheets().addStylesheet(styleSheet); // TODO: Get the node's document
  }

}
