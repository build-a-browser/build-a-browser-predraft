package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrieView;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeBuffer;

// TODO: Might be efficient to cache partial match states (like a trie)
public class TokenizeBufferImp implements TokenizeBuffer {

  private final StringBuilder stringBuilder = new StringBuilder();

  private MatchTrieView lookahead = null;
  private String lastMatch = null;

  @Override
  public boolean continues() {
    if (lookahead.hasMatch()) {
      this.lastMatch = stringBuilder.toString();
    }

    return lookahead.continues() || lookahead.hasMatch();
  }

  @Override
  public String dump() {
    return stringBuilder.toString();
  }

  @Override
  public void reset() {
    stringBuilder.setLength(0);
    this.lookahead = null;
    this.lastMatch = null;
  }

  @Override
  public void appendCodePoint(int codepoint) {
    assert lookahead != null;
    stringBuilder.appendCodePoint(codepoint);
    lookahead.narrow(ASCIIUtil.toUpper(codepoint));
  }

  @Override
  public String lastMatch() {
    return this.lastMatch;
  }

  @Override
  public void markLookahead(MatchTrie lookaheadOptions) {
    if (this.lookahead != null) return;
    this.lookahead = lookaheadOptions.createView();
  }
  
}
