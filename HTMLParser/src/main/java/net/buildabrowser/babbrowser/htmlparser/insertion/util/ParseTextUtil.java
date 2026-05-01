package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.html.HTMLText;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class ParseTextUtil {
  
  private ParseTextUtil() {}

  public static void insertACharacter(ParseContext parseContext, int ch) {
    Node adjustedInsertionLocation = ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, null);
    if (adjustedInsertionLocation instanceof Document) return;

    if (adjustedInsertionLocation.lastChild() instanceof Text text) {
      text.appendCharacter(ch);
    } else {
      Text text = HTMLText.create("");
      adjustedInsertionLocation.appendChild(text);
      text.appendCharacter(ch);
    }
  }

  public static void insertAString(ParseContext parseContext, String data) {
    Node adjustedInsertionLocation = ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, null);
    if (adjustedInsertionLocation instanceof Document) return;

    if (adjustedInsertionLocation.lastChild() instanceof Text text) {
      text.appendString(data);
    } else {
      Text text = HTMLText.create("");
      adjustedInsertionLocation.appendChild(text);
      text.appendString(data);
    }
  }

  public static void reconstructTheActiveFormattingElements(ParseContext parseContext) {
    // TODO: Implement the algorithm
  }
  
}
