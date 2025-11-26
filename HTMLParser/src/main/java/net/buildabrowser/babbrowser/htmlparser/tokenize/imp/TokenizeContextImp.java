package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import java.io.IOException;
import java.io.PushbackReader;

import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.token.imp.TagTokenImp;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class TokenizeContextImp implements TokenizeContext {

  private final PushbackReader pushbackReader;
  private final TemporaryBuffer temporaryBuffer = new TemporaryBufferImp();
  private final TagTokenImp tagToken = new TagTokenImp();

  private TokenizeState tokenizeState = TokenizeStates.dataState;

  public TokenizeContextImp(PushbackReader pushbackReader) {
    this.pushbackReader = pushbackReader;
  }

  @Override
  public void setTokenizeState(TokenizeState tokenizeState) {
    this.tokenizeState = tokenizeState;
  }

  @Override
  public void reconsumeInTokenizeState(int ch, TokenizeState tokenizeState) throws IOException {
    this.tokenizeState = tokenizeState;
    pushbackReader.unread(ch);
  }

  @Override
  public TokenizeState getTokenizeState() {
    return this.tokenizeState;
  }

  @Override
  public TemporaryBuffer temporaryBuffer() {
    return temporaryBuffer;
  }

  @Override
  public TagToken beginTagToken(boolean isStartTag) {
    tagToken.reinit(isStartTag);
    return tagToken;
  }

  @Override
  public TagToken currentTagToken() {
    return this.tagToken;
  }

  private class TemporaryBufferImp implements TemporaryBuffer {

    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void append(int ch) {
      buffer.appendCodePoint(ch);
    }

    @Override
    public String get() {
      return buffer.toString();
    }

    @Override
    public void clear() {
      buffer.setLength(0);
    }
    
  }
  
}
