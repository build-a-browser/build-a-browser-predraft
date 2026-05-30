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
            parseContext.setInsertionMode(InsertionModes.inCellInsertionMode);
            return;
          }
          break;
        case "tr":
          parseContext.setInsertionMode(InsertionModes.inRowInsertionMode);
          return;
        case "tbody", "thead", "tfoot":
          parseContext.setInsertionMode(InsertionModes.inTableBodyInsertionMode);
          return;
        case "caption":
          parseContext.setInsertionMode(InsertionModes.inCaptionInsertionMode);
          return;
        case "colgroup":
          parseContext.setInsertionMode(InsertionModes.inColumnGroupInsertionMode);
          return;
        case "table":
          parseContext.setInsertionMode(InsertionModes.inTableInsertionMode);
          return;
        // TODO: Template
        case "head":
          if (!last) {
            parseContext.setInsertionMode(InsertionModes.inHeadInsertionMode);
            return;
          }
          break;
        case "body":
          parseContext.setInsertionMode(InsertionModes.inBodyInsertionMode);
          return;
        // TODO: Frameset
        case "html":
          if (parseContext.headElementPointer() == null) {
            parseContext.setInsertionMode(InsertionModes.beforeHeadInsertionMode);
          } else {
            parseContext.setInsertionMode(InsertionModes.afterHeadInsertionMode);
          }
          return;
      }

      if (last) {
        parseContext.setInsertionMode(InsertionModes.inBodyInsertionMode);
        return;
      }

      node = stackIt.next();
    }
  }

}
