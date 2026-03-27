package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class ParseTextUtil {
  
  private ParseTextUtil() {}

  public static void insertACharacter(ParseContext parseContext, int ch) {
    Node adjustedInsertionLocation = ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, null);
    if (adjustedInsertionLocation instanceof Document) return;

    if (adjustedInsertionLocation.lastChild() instanceof Text text) {
      text.appendCharacter(ch);
    } else {
      Text text = Text.create("");
      adjustedInsertionLocation.appendChild(text);
      text.appendCharacter(ch);
    }
  }

  public static void reconstructTheActiveFormattingElements(ParseContext parseContext) {
    // TODO: Implement the algorithm
  }
  
}
