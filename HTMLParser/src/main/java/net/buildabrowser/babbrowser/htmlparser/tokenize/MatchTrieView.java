package net.buildabrowser.babbrowser.htmlparser.tokenize;

public interface MatchTrieView {
  
  void narrow(int ch);

  boolean hasMatch();

  boolean continues();

}
