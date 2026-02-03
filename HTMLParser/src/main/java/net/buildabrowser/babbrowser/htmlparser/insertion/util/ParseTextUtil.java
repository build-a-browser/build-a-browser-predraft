package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.dom.mutable.MutableText;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class ParseTextUtil {
  
  private ParseTextUtil() {}

  public static void insertACharacter(ParseContext parseContext, int ch) {
    MutableNode adjustedInsertionLocation = ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, null);
    if (adjustedInsertionLocation instanceof Document) return;

    if (adjustedInsertionLocation.lastChild() instanceof MutableText text) {
      text.appendCharacter(ch);
    } else {
      MutableText text = MutableText.create("");
      adjustedInsertionLocation.appendChild(text);
      text.appendCharacter(ch);
    }
  }

  public static void reconstructTheActiveFormattingElements(ParseContext parseContext) {
    // TODO: Implement the algorithm
  }
  
}
