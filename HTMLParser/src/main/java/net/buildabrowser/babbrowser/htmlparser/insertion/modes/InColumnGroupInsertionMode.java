package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseTextUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InColumnGroupInsertionMode implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    switch (ch) {
    case '\t', '\n', '\f', '\r', ' ':
      ParseTextUtil.insertACharacter(parseContext, ch);
      return false;
    default:
      return handleAnythingElse(parseContext);
    }
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    return handleAnythingElse(parseContext);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    ParseCommentUtil.insertAComment(parseContext, commentToken);
    return false;
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    parseContext.parseError();
    return false;
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag()) {
      return emitStartTagToken(parseContext, tagToken);
    } else {
      return emitEndTagToken(parseContext, tagToken);
    }
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    return InsertionModes.inBodyInsertionMode.emitEOFToken(parseContext);
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "html":
      return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
    case "col":
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.openElementStack().popNode();
      if (tagToken.isSelfClosing()) {
        tagToken.acknowledgeSelfClosingFlag();
      }
      return false;
    case "template":
      return InsertionModes.inHeadInsertionMode.emitTagToken(parseContext, tagToken);
    default:
      return handleAnythingElse(parseContext);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "colgroup":
      // Handling is the same as for "anything else", except do not reprocess
      handleAnythingElse(parseContext);
      return false;
    case "col":
      parseContext.parseError();
      return false;
    case "template":
      return InsertionModes.inHeadInsertionMode.emitTagToken(parseContext, tagToken);
    default:
      return handleAnythingElse(parseContext);
    }
  }

  private boolean handleAnythingElse(ParseContext parseContext) {
    if (!(
      parseContext.openElementStack().peek() instanceof HTMLElement element
      && element.name().equals("colgroup")
    )) {
      parseContext.parseError();
      return false;
    }

    parseContext.openElementStack().popNode();
    parseContext.setInsertionMode(InsertionModes.inTableInsertionMode);
    return true;
  }
  
}
