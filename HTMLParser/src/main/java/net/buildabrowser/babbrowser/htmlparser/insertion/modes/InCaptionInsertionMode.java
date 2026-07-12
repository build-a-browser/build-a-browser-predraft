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

public class InCaptionInsertionMode implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitCharacterToken(parseContext, ch);
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitOptimizedString(parseContext, data);
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitEOFToken(parseContext);
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag()) {
      return handleStartTagToken(parseContext, tagToken);
    } else {
      return handleEndTagToken(parseContext, tagToken);
    }
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitDoctypeToken(parseContext, doctypeToken);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitCommentToken(parseContext, commentToken);
  }

  private boolean handleStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr":
      return closeCaption(parseContext, true);
    default:
      return InsertionModes.IN_BODY_INSERTION_MODE.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean handleEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "caption":
      return closeCaption(parseContext, false);
    case "table":
      return closeCaption(parseContext, true);
    case "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr":
      parseContext.parseError();
      return false;
    default:
      return InsertionModes.IN_BODY_INSERTION_MODE.emitTagToken(parseContext, tagToken);
    }
  }

  private boolean closeCaption(ParseContext parseContext, boolean reprocess) {
    OpenElementStack stack = parseContext.openElementStack();
    if (!ParseAdjustUtil.hasInTableScope(stack, "caption")) {
      parseContext.parseError();
      return false;
    }

    ParseAdjustUtil.generateImpliedEndTags(stack);
    if (!(
      stack.peek() instanceof HTMLElement element
      && element.name().equals("caption")
    )) parseContext.parseError();

    ParseAdjustUtil.popUntil(stack, "caption");

    ParseTextUtil.clearActiveFormattingElementsToLastMarker(parseContext);
    parseContext.setInsertionMode(InsertionModes.IN_TABLE_INSERTION_MODE);

    return reprocess;
  }
  
}
