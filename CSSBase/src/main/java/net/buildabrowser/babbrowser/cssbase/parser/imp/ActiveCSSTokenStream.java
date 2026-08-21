package net.buildabrowser.babbrowser.cssbase.parser.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.removeLast;

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

  public ActiveCSSTokenStream(
    CSSTokenStreamSource source,
    CSSTokenizerInput tokenizerInput
  ) {
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
    } else if (
      tokenPushback.size() > 0
      && markPushback.size() == 0
    ) {
      tokenPushback.clear();
    }

    Token token = cssTokenizer.consumeAToken(tokenizerInput);
    if (markPushback.size() > 0) {
      tokenPushback.add(token);
      markPos++;
    }

    return token;
  }

  @Override
  public void unread(Token token) {
    this.pushback = token;
  }

  //

  @Override
  public int mark() {
    if (markPushback.size() == 0) {
      markPos = 0;
    }

    markPushback.add(markPos);
    
    return markPos;
  }

  @Override
  public void restoreMark(int mark) {
    this.pushback = null;
    int removed = removeLast(markPushback);
    assert mark == removed;
    this.markPos = mark;
  }

  @Override
  public void discardMark() {
    this.pushback = null;
    removeLast(markPushback);
    if (
      markPushback.size() == 0
      && markPos >= tokenPushback.size()
    ) {
      markPos = 0;
      tokenPushback.clear();
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
