package net.buildabrowser.babbrowser.htmlparser.tokenize;

import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeBufferImp;

public interface TokenizeBuffer {
  
  boolean continues(List<String> options);

  String matches(List<String> options);

  String dump();

  void reset();

  void appendCodePoint(int codepoint);

  static TokenizeBuffer create() {
    return new TokenizeBufferImp();
  }

}
