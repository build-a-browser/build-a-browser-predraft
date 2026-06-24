package net.buildabrowser.babbrowser.htmlparser.imp;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeBuffer;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class HTMLParserImp implements HTMLParser {

  private static final int EOF = -1;

  private final CharBuffer chars = CharBuffer.allocate(2048);

  private final TokenizeContext tokenizeContext;
  private final ParseContext parseContext;
  private final TokenizeBuffer tokenizeBuffer;
  private final RollingCharsetDecoder charsetDecoder;

  private boolean didUseCharsetDecoder = false;
  private int[] pushbackBuffer = new int[16];
  private int pushbackPos = -1;

  public HTMLParserImp(Document document, Charset charset) {
    this.tokenizeContext = TokenizeContext.create(this::pushback);
    this.parseContext = ParseContext.create(document, tokenizeContext);
    this.tokenizeBuffer = TokenizeBuffer.create();
    this.charsetDecoder = new RollingCharsetDecoder(charset.newDecoder()
      .onMalformedInput(CodingErrorAction.REPLACE)
      .onUnmappableCharacter(CodingErrorAction.REPLACE));
  }

  @Override
  public void parse(ByteBuffer buffer) {
    this.didUseCharsetDecoder = true;
    charsetDecoder.decode(buffer, chars,
      () -> parseCharBuffer(chars));
  }

  @Override
  public void parse(int ch) {
    TokenizeState tokenizeState = tokenizeContext.getTokenizeState();
    MatchTrie lookaheadOptions = tokenizeState.lookaheadOptions();

    if (ch == -1 && !tokenizeBuffer.dump().isEmpty()) {
      endBuffer(tokenizeContext, parseContext, tokenizeBuffer);
    } else if (lookaheadOptions == null || ch == -1) {
      tokenizeState.consume(ch, tokenizeContext, parseContext);
    } else {
      tokenizeBuffer.markLookahead(lookaheadOptions);
      tokenizeBuffer.appendCodePoint(ch);
      if (!tokenizeBuffer.continues()) {
        endBuffer(tokenizeContext, parseContext, tokenizeBuffer);
      }
    }

    while (pushbackPos > -1) {
      int ch2 = pushbackBuffer[pushbackPos];
      pushbackPos--;
      parse(ch2);
    }
  }

  @Override
  public void done() {
    if (didUseCharsetDecoder) {
      charsetDecoder.flush(chars, () -> parseCharBuffer(chars));
    }
    
    parse(EOF);
  }

  private void parseCharBuffer(CharBuffer chars) {
    for (int i = 0; i < chars.remaining(); ) {
      int codePoint = Character.codePointAt(chars, i);
      parse(codePoint);
      i += Character.charCount(codePoint); 
    }
  }

  private void endBuffer(TokenizeContext tokenizeContext, ParseContext parseContext, TokenizeBuffer tokenizeBuffer) {
    TokenizeState tokenizeState = tokenizeContext.getTokenizeState();

    String matched = tokenizeBuffer.lastMatch();
    String tmpbuf = tokenizeBuffer.dump();
    tokenizeBuffer.reset();
    if (tmpbuf.isEmpty()) return;

    if (
      matched != null
      && tokenizeState.lookaheadMatched(matched, tokenizeContext, parseContext)
    ) {
      for (int i = tmpbuf.length() - 1; i >= matched.length(); i--) {
        pushback(tmpbuf.codePointAt(i));
      }
      return;
    }

    for (int i = tmpbuf.length() - 1; i >= 1; i--) {
      pushback(tmpbuf.codePointAt(i));
    }

    tokenizeContext.getTokenizeState().consume(
      tmpbuf.codePointAt(0), tokenizeContext, parseContext);
  }

  private void pushback(int codepoint) {
    if (pushbackPos == pushbackBuffer.length - 1) {
      throw new UnsupportedOperationException("Cannot pushback character that would exceed buffer size");
    } else {
      pushbackBuffer[++pushbackPos] = codepoint;
    }
  }

}
