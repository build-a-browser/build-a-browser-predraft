package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseAdjustUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InRowInsertionMode implements InsertionMode {

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag()) {
      return emitStartTagToken(parseContext, tagToken);
    } else {
      return emitEndTagToken(parseContext, tagToken);
    }
  }

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    return InsertionModes.inTableInsertionMode.emitCharacterToken(parseContext, ch);
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    return InsertionModes.inTableInsertionMode.emitOptimizedString(parseContext, data);
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    return InsertionModes.inTableInsertionMode.emitEOFToken(parseContext);
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    return InsertionModes.inTableInsertionMode.emitDoctypeToken(parseContext, doctypeToken);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    return InsertionModes.inTableInsertionMode.emitCommentToken(parseContext, commentToken);
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "th", "td":
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.setInsertionMode(InsertionModes.inCellInsertionMode);
      // TODO: Insert a marker
      return false;
    case "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr":
      return closeTableRow(parseContext, true, true);
    default:
      return InsertionModes.inTableInsertionMode.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "tr":
      return closeTableRow(parseContext, false, true);
    case "table":
      return closeTableRow(parseContext, true, true);
    case "tbody", "tfoot", "thead":
      if (!ParseAdjustUtil.hasInTableScope(
        parseContext.openElementStack(), tagToken.name()
      )) {
        parseContext.parseError();
        return false;
      }

      return closeTableRow(parseContext, true, false);
    case "body", "caption", "col", "colgroup", "html", "td", "th":
      parseContext.parseError();
      return false;
    default:
      return InsertionModes.inTableInsertionMode.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean closeTableRow(
    ParseContext parseContext, boolean shouldReconsume, boolean needsTr
  ) {
    OpenElementStack stack = parseContext.openElementStack();
    if (!(
      ParseAdjustUtil.hasInTableScope(stack, "tr")
    )) {
      if (needsTr) parseContext.parseError();
      return false;
    }

    clearStackBackToTableContext(parseContext);
    stack.popNode();
    parseContext.setInsertionMode(InsertionModes.inTableBodyInsertionMode);

    return shouldReconsume;
  }

  private void clearStackBackToTableContext(ParseContext parseContext) {
    OpenElementStack stack = parseContext.openElementStack();
    while (!(
      stack.peek() instanceof HTMLElement element
      && (
        element.name().equals("tr")
        || element.name().equals("template")
        || element.name().equals("html"))
    )) {
      stack.popNode();
    }
  }
  
}
