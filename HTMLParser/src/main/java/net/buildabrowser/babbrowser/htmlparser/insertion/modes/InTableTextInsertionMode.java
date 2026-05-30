package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InTableTextInsertionMode implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    if (ch == 0) {
      parseContext.parseError();
      return false;
    }

    // TODO: Append to list
    return false;
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    // TODO
    return false;
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    return handleAnythingElse(parseContext);
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    return handleAnythingElse(parseContext);
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    return handleAnythingElse(parseContext);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    return handleAnythingElse(parseContext);
  }

  private boolean handleAnythingElse(ParseContext parseContext) {
    // TODO
    parseContext.setInsertionMode(parseContext.originalInsertionMode());
    return true;
  }
  
}
