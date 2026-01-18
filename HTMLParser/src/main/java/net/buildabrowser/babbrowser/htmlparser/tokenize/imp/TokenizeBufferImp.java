package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeBuffer;

// TODO: Might be efficient to cache partial match states (like a trie)
public class TokenizeBufferImp implements TokenizeBuffer {

  private final StringBuilder stringBuilder = new StringBuilder();
  private String lastMatch = null;

  @Override
  public boolean continues(List<String> options) {
    String currentString = stringBuilder.toString();
    String currentUpper = currentString.toUpperCase();
    for (String option: options) {
      String optionUpper = option.toUpperCase();
      if (optionUpper.startsWith(currentUpper)) {
        if (optionUpper.equals(currentUpper)) {
          this.lastMatch = currentString;
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public String dump() {
    return stringBuilder.toString();
  }

  @Override
  public void reset() {
    stringBuilder.setLength(0);
    this.lastMatch = null;
  }

  @Override
  public void appendCodePoint(int codepoint) {
    stringBuilder.appendCodePoint(codepoint);
  }

  @Override
  public String lastMatch() {
    return this.lastMatch;
  }
  
}
