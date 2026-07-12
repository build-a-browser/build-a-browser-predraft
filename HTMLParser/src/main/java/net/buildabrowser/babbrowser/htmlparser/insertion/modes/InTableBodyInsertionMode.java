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

public class InTableBodyInsertionMode implements InsertionMode {

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
    return InsertionModes.IN_TABLE_INSERTION_MODE.emitCharacterToken(parseContext, ch);
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    return InsertionModes.IN_TABLE_INSERTION_MODE.emitOptimizedString(parseContext, data);
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    return InsertionModes.IN_TABLE_INSERTION_MODE.emitEOFToken(parseContext);
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    return InsertionModes.IN_TABLE_INSERTION_MODE.emitDoctypeToken(parseContext, doctypeToken);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    return InsertionModes.IN_TABLE_INSERTION_MODE.emitCommentToken(parseContext, commentToken);
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "tr":
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.setInsertionMode(InsertionModes.IN_ROW_INSERTION_MODE);
      return false;
    case "th", "td":
      parseContext.parseError();
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(
        parseContext, TagToken.create(true, "tr"));
      parseContext.setInsertionMode(InsertionModes.IN_ROW_INSERTION_MODE);
      return true;
    case "caption", "col", "colgroup", "tbody", "tfoot", "thead":
      return closeTableBody(parseContext, true);
    default:
      return InsertionModes.IN_TABLE_INSERTION_MODE.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "tbody", "tfoot", "thead":
      return closeTableBody(parseContext, false);
    case "table":
      return closeTableBody(parseContext, true);
    case "body", "caption", "col", "colgroup", "html", "td", "th", "tr":
      parseContext.parseError();
      return false;
    default:
      return InsertionModes.IN_TABLE_INSERTION_MODE.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean closeTableBody(ParseContext parseContext, boolean shouldReconsume) {
    OpenElementStack stack = parseContext.openElementStack();
    if (!(
      ParseAdjustUtil.hasInTableScope(stack, "tbody")
      || ParseAdjustUtil.hasInTableScope(stack, "thead")
      || ParseAdjustUtil.hasInTableScope(stack, "tfoot")
    )) {
      parseContext.parseError();
      return false;
    }

    clearStackBackToTableContext(parseContext);
    stack.popNode();
    parseContext.setInsertionMode(InsertionModes.IN_TABLE_INSERTION_MODE);

    return shouldReconsume;
  }

  private void clearStackBackToTableContext(ParseContext parseContext) {
    OpenElementStack stack = parseContext.openElementStack();
    while (!(
      stack.peek() instanceof HTMLElement element
      && (
        element.name().equals("tbody")
        || element.name().equals("tfoot")
        || element.name().equals("thead")
        || element.name().equals("template")
        || element.name().equals("html"))
    )) {
      stack.popNode();
    }
  }
  
}
