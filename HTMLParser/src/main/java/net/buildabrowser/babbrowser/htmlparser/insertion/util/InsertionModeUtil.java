package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import java.util.Iterator;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class InsertionModeUtil {
  
  private InsertionModeUtil() {}

  public static void resetInsertionModeAppropriately(ParseContext parseContext) {
    OpenElementStack stack = parseContext.openElementStack();

    Iterator<Node> stackIt = stack.iterator();
    Node node = stackIt.next();
    while (true) {
      boolean last = !stackIt.hasNext();
      // TODO: Fragment parsing algo?
      // TODO: Use qualified names
      String elName = node instanceof Element element ? element.name() : null;
      if (elName != null) switch (elName) {
        case "td", "th":
          if (!last) {
            parseContext.setInsertionMode(InsertionModes.IN_CELL_INSERTION_MODE);
            return;
          }
          break;
        case "tr":
          parseContext.setInsertionMode(InsertionModes.IN_ROW_INSERTION_MODE);
          return;
        case "tbody", "thead", "tfoot":
          parseContext.setInsertionMode(InsertionModes.IN_TABLE_BODY_INSERTION_MODE);
          return;
        case "caption":
          parseContext.setInsertionMode(InsertionModes.IN_CAPTION_INSERTION_MODE);
          return;
        case "colgroup":
          parseContext.setInsertionMode(InsertionModes.IN_COLUMN_GROUP_INSERTION_MODE);
          return;
        case "table":
          parseContext.setInsertionMode(InsertionModes.IN_TABLE_INSERTION_MODE);
          return;
        // TODO: Template
        case "head":
          if (!last) {
            parseContext.setInsertionMode(InsertionModes.IN_HEAD_INSERTION_MODE);
            return;
          }
          break;
        case "body":
          parseContext.setInsertionMode(InsertionModes.IN_BODY_INSERTION_MODE);
          return;
        // TODO: Frameset
        case "html":
          if (parseContext.headElementPointer() == null) {
            parseContext.setInsertionMode(InsertionModes.BEFORE_HEAD_INSERTION_MODE);
          } else {
            parseContext.setInsertionMode(InsertionModes.AFTER_HEAD_INSERTION_MODE);
          }
          return;
      }

      if (last) {
        parseContext.setInsertionMode(InsertionModes.IN_BODY_INSERTION_MODE);
        return;
      }

      node = stackIt.next();
    }
  }

}
