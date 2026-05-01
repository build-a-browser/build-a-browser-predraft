package net.buildabrowser.babbrowser.htmlparser.tokenize;

import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeBufferImp;

public interface TokenizeBuffer {
  
  boolean continues();

  String lastMatch();

  String dump();

  void reset();

  void appendCodePoint(int codepoint);

  void markLookahead(MatchTrie lookaheadOptions);

  static TokenizeBuffer create() {
    return new TokenizeBufferImp();
  }

}
