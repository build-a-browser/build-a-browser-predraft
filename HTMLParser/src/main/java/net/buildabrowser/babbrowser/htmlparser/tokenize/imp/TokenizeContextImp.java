package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import java.io.IOException;
import java.io.PushbackReader;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.token.imp.CommentTokenImp;
import net.buildabrowser.babbrowser.htmlparser.token.imp.TagTokenImp;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class TokenizeContextImp implements TokenizeContext {

  private final PushbackReader pushbackReader;
  private final TemporaryBuffer temporaryBuffer = new TemporaryBufferImp();
  private final TagTokenImp tagToken = new TagTokenImp();
  private final DoctypeToken doctypeToken = DoctypeToken.create();
  private final CommentToken commentToken = CommentToken.create();

  private TokenizeState tokenizeState = TokenizeStates.dataState;
  private TokenizeState returnState;
  private int charRefCode;

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
  public void setReturnState(TokenizeState returnState) {
    this.returnState = returnState;
  }

  @Override
  public TokenizeState getReturnState() {
    return this.returnState;
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

  @Override
  public DoctypeToken beginDoctypeToken() {
    return this.doctypeToken; // TODO: Re-init
  }

  @Override
  public DoctypeToken currentDoctypeToken() {
    return this.doctypeToken;
  }

  @Override
  public CommentToken beginCommentToken() {
    ((CommentTokenImp) commentToken).reset();
    return this.commentToken;
  }
  @Override
  public CommentToken currentCommentToken() {
    return this.commentToken;
  }

  @Override
  public void setCharacterReferenceCode(int code) {
    this.charRefCode = code;
  }

  @Override
  public int getCharacterReferenceCode() {
    return this.charRefCode;
  }

  @Override
  public void flushCodePointsConsumedAsACharacterReference(ParseContext parseContext) {
    String valueToFlush = temporaryBuffer.get();
    if (returnState.equals(TokenizeStates.dataState) || returnState.equals(TokenizeStates.rcdataState)) {
      for (int i = 0; i < valueToFlush.length(); i++) {
        parseContext.emitCharacterToken(valueToFlush.codePointAt(i));
      }
    } else {
      for (int i = 0; i < valueToFlush.length(); i++) {
        currentTagToken().appendToAttributeValue(valueToFlush.codePointAt(i));
      }
    }
  }

  private class TemporaryBufferImp implements TemporaryBuffer {

    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void append(int ch) {
      buffer.appendCodePoint(ch);
    }

    @Override
    public void append(String str) {
      buffer.append(str);
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
