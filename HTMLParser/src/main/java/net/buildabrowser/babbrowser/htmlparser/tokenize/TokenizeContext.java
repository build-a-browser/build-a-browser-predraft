package net.buildabrowser.babbrowser.htmlparser.tokenize;

import java.io.IOException;
import java.io.PushbackReader;

import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeContextImp;

public interface TokenizeContext {

  static int EOF = -1;
  
  void setTokenizeState(TokenizeState tokenizeState);

  void reconsumeInTokenizeState(int ch, TokenizeState tokenizeState) throws IOException;

  TokenizeState getTokenizeState();
  
  TemporaryBuffer temporaryBuffer();

  TagToken beginTagToken(boolean isStartTag);

  TagToken currentTagToken();

  interface TemporaryBuffer {

    void append(int ch);

    String get();

    void clear();

  }

  public static TokenizeContext create(PushbackReader pushbackReader) {
    return new TokenizeContextImp(pushbackReader);
  }

}
