package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizer;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ActiveCSSTokenStream implements CSSTokenStream {
  
private final CSSTokenizer cssTokenizer = CSSTokenizer.create();
  private final List<Token> tokenPushback = new ArrayList<>();
  private final List<Integer> markPushback = new ArrayList<>();
  private final CSSTokenStreamSource source;
  private final CSSTokenizerInput tokenizerInput;

  private Token pushback = null;
  private int markPos = 0;

  public ActiveCSSTokenStream(CSSTokenStreamSource source, CSSTokenizerInput tokenizerInput) {
    this.source = source;
    this.tokenizerInput = tokenizerInput;
  }

  @Override
  public CSSTokenStreamSource source() {
    return this.source;
  }

  @Override
  public Token read() throws IOException {
    if (this.pushback != null) {
      Token rtn = this.pushback;
      this.pushback = null;
      return rtn;
    }

    if (markPos < tokenPushback.size()) {
      return tokenPushback.get(markPos++);
    }

    if (markPushback.isEmpty() && !tokenPushback.isEmpty()) {
      tokenPushback.clear();
      markPos = 0;
    }

    Token token = cssTokenizer.consumeAToken(tokenizerInput);
    if (!markPushback.isEmpty()) {
      tokenPushback.add(token);
      markPos++;
    }

    return token;
  }

  @Override
  public void unread(Token token) {
    if (markPos > 0 && !tokenPushback.isEmpty()) {
      markPos--;
    } else {
      this.pushback = token;
    }
  }

  @Override
  public int mark() {
    if (markPushback.isEmpty() && markPos >= tokenPushback.size()) {
      tokenPushback.clear();
      markPos = 0;
    }

    markPushback.add(markPos);
    return markPos;
  }

  @Override
  public void restoreMark(int mark) {
    this.pushback = null;
    int removed = markPushback.remove(markPushback.size() - 1);
    assert mark == removed;
    this.markPos = mark;
  }

  @Override
  public void discardMark() {
    this.pushback = null;
    if (!markPushback.isEmpty()) {
      markPushback.remove(markPushback.size() - 1);
    }
    if (markPushback.isEmpty() && markPos >= tokenPushback.size()) {
      tokenPushback.clear();
      markPos = 0;
    }
  }

  @Override
  public int nextMark() {
    return markPos;
  }

  @Override
  public void seek(int markRelPosition) {
    this.markPos = markRelPosition;
  }

}
