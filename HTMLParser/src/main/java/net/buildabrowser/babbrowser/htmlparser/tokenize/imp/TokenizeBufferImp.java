package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeBuffer;

// TODO: Might be efficient to cache partial match states
public class TokenizeBufferImp implements TokenizeBuffer {

  private final StringBuilder stringBuilder = new StringBuilder();

  @Override
  public boolean continues(List<String> options) {
    for (String option: options) {
      String currentString = stringBuilder.toString();
      if (option.toUpperCase().startsWith(currentString.toUpperCase())) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String matches(List<String> options) {
    for (String option: options) {
      String currentString = stringBuilder.toString();
      if (currentString.toUpperCase().equals(option.toUpperCase())) {
        return currentString;
      }
    }

    return null;
  }

  @Override
  public String dump() {
    return stringBuilder.toString();
  }

  @Override
  public void reset() {
    stringBuilder.setLength(0);
  }

  @Override
  public void appendCodePoint(int codepoint) {
    stringBuilder.appendCodePoint(codepoint);
  }
  
}
