package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseAdjustUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseTextUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InCellInsertionMode implements InsertionMode {

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
    return InsertionModes.inBodyInsertionMode.emitCharacterToken(parseContext, ch);
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    return InsertionModes.inBodyInsertionMode.emitOptimizedString(parseContext, data);
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    return InsertionModes.inBodyInsertionMode.emitEOFToken(parseContext);
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    return InsertionModes.inBodyInsertionMode.emitDoctypeToken(parseContext, doctypeToken);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    return InsertionModes.inBodyInsertionMode.emitCommentToken(parseContext, commentToken);
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr":
      assert
        ParseAdjustUtil.hasInTableScope(parseContext.openElementStack(), "td")
        || ParseAdjustUtil.hasInTableScope(parseContext.openElementStack(), "th");
      return closeTheCell(parseContext);
    default:
      return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "td", "th": {
      OpenElementStack stack = parseContext.openElementStack();
      if (!ParseAdjustUtil.hasInTableScope(stack, tagToken.name())) {
        parseContext.parseError();
        return false;
      }

      ParseAdjustUtil.generateImpliedEndTags(stack);
      if (!(
        stack.peek() instanceof HTMLElement element
        && element.name().equals(tagToken.name())
      )) parseContext.parseError();
      ParseAdjustUtil.popUntil(stack, tagToken.name());
      ParseTextUtil.clearActiveFormattingElementsToLastMarker(parseContext);
      parseContext.setInsertionMode(InsertionModes.inRowInsertionMode);

      return false;
    }
    case "body", "caption", "col", "colgroup", "html":
      parseContext.parseError();
      return false;
    case "table", "tbody", "tfoot", "thead", "tr": {
      OpenElementStack stack = parseContext.openElementStack();
      if (!ParseAdjustUtil.hasInTableScope(stack, tagToken.name())) {
        parseContext.parseError();
        return false;
      }

      return closeTheCell(parseContext);
    }
    default:
      return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean closeTheCell(ParseContext parseContext) {
    OpenElementStack stack = parseContext.openElementStack();
    ParseAdjustUtil.generateImpliedEndTags(stack);
    if (!(
      stack.peek() instanceof HTMLElement element
      && (
        element.name().equals("td")
        || element.name().equals("th"))
    )) parseContext.parseError();
    while (!(
      stack.popNode() instanceof HTMLElement element
      && (
        element.name().equals("td")
        || element.name().equals("th"))
    ));
    ParseTextUtil.clearActiveFormattingElementsToLastMarker(parseContext);
    parseContext.setInsertionMode(InsertionModes.inRowInsertionMode);

    return true;
  }
  
}
