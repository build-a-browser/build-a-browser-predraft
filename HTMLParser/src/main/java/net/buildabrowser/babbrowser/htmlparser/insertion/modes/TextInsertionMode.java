package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseTextUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class TextInsertionMode implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    ParseTextUtil.insertACharacter(parseContext, ch);
    return false;
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    throw new IllegalStateException("Unexpected Comment");
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    throw new IllegalStateException("Unexpected Doctype");
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    parseContext.parseError();
    // TODO: Handle script
    parseContext.openElementStack().popNode();
    parseContext.setInsertionMode(parseContext.originalInsertionMode());
    return true;
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag()) {
      throw new IllegalStateException("Unexpected Start Tag");
    }

    // TODO: Handle script end tag
    parseContext.openElementStack().popNode();
    parseContext.setInsertionMode(parseContext.originalInsertionMode());
    return false;
  }
  
}
