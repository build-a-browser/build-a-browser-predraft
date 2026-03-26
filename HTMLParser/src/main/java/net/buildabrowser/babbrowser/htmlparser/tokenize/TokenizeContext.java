package net.buildabrowser.babbrowser.htmlparser.tokenize;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeContextImp;

public interface TokenizeContext {

  static int EOF = -1;
  
  void setTokenizeState(TokenizeState tokenizeState);

  void reconsumeInTokenizeState(int ch, TokenizeState tokenizeState);

  TokenizeState getTokenizeState();

  void setReturnState(TokenizeState returnState);

  TokenizeState getReturnState();
  
  TemporaryBuffer temporaryBuffer();

  TagToken beginTagToken(boolean isStartTag);

  TagToken currentTagToken();

  DoctypeToken beginDoctypeToken();

  DoctypeToken currentDoctypeToken();

  CommentToken beginCommentToken();

  CommentToken currentCommentToken();

  void setCharacterReferenceCode(int i);

  int getCharacterReferenceCode();

  void flushCodePointsConsumedAsACharacterReference(ParseContext parseContext);

  interface TemporaryBuffer {

    void append(int ch);

    void append(String str);

    String get();

    void clear();

  }

  interface Pushback {

    void unread(int ch);

  }

  public static TokenizeContext create(Pushback pushback) {
    return new TokenizeContextImp(pushback);
  }

}
