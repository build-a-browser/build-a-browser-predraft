package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class AfterAfterBodyInsertionMode implements InsertionMode {

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    ParseCommentUtil.insertAComment(parseContext, commentToken, parseContext.document());
    return false;
  }

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    switch (ch) {
      case '\t', '\n', '\f', '\r', ' ':
        return InsertionModes.inBodyInsertionMode.emitCharacterToken(parseContext, ch);
      default:
        return handleAnythingElse(parseContext);
    }
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    InsertionModes.inBodyInsertionMode.emitDoctypeToken(parseContext, doctypeToken);
    return false;
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    parseContext.stopParsing();
    return false;
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag() && tagToken.name().equals("html")) {
      return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
    } else {
      return handleAnythingElse(parseContext);
    }
  }

  private boolean handleAnythingElse(ParseContext parseContext) {
    parseContext.parseError();
    parseContext.setInsertionMode(InsertionModes.inBodyInsertionMode);
    return true;
  }
  
}
