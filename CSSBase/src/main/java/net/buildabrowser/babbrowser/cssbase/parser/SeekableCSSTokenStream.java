package net.buildabrowser.babbrowser.cssbase.parser;

public interface SeekableCSSTokenStream extends CSSTokenStream {

  int position();

  void seek(int position);
  
}