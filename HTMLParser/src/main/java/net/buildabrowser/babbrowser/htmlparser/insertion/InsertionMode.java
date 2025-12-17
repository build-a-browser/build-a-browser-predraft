package net.buildabrowser.babbrowser.htmlparser.insertion;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public interface InsertionMode {

  boolean emitCharacterToken(ParseContext parseContext, int ch);

  boolean emitEOFToken(ParseContext parseContext);

  boolean emitTagToken(ParseContext parseContext, TagToken tagToken);

  boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken);

  boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken);
  
}
