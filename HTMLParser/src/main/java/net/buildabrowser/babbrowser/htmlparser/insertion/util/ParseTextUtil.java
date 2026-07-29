package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.html.HTMLText;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil.AdjustedInsertionLocation;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class ParseTextUtil {
  
  private ParseTextUtil() {}

  public static void insertACharacter(ParseContext parseContext, int ch) {
    AdjustedInsertionLocation adjustedInsertionLocation =
      ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, null);
    if (adjustedInsertionLocation.parentNode() instanceof Document) return;

    if (adjustedInsertionLocation.afterNode() instanceof Text text) {
      text.appendCharacter(ch);
      text.nodeDocument().changeListener().onNodeAdded(text);
    } else {
      Text text = HTMLText.create("");
      ParseElementUtil.insertNodeAt(text, adjustedInsertionLocation);
      text.appendCharacter(ch);
    }
  }

  public static void insertAString(ParseContext parseContext, String data) {
    AdjustedInsertionLocation adjustedInsertionLocation =
      ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, null);
    if (adjustedInsertionLocation.parentNode() instanceof Document) return;

    if (adjustedInsertionLocation.afterNode() instanceof Text text) {
      text.appendString(data);
      text.nodeDocument().changeListener().onNodeAdded(text);
    } else {
      Text text = HTMLText.create("");
      ParseElementUtil.insertNodeAt(text, adjustedInsertionLocation);
      text.appendString(data);
    }
  }

  public static void reconstructTheActiveFormattingElements(ParseContext parseContext) {
    // TODO: Implement the algorithm
  }

  public static void clearActiveFormattingElementsToLastMarker(ParseContext parseContext) {
    // TODO: Implement the algorithm
  }
  
}
