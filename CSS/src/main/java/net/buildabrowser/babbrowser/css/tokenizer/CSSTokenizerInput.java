package net.buildabrowser.babbrowser.css.tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.buildabrowser.babbrowser.css.tokenizer.imp.ReaderCSSTokenizerInput;

public interface CSSTokenizerInput {

  int read() throws IOException;

  void unread(int ch) throws IOException;

  int peek() throws IOException;
  
  static CSSTokenizerInput fromReader(Reader reader) {
    return new ReaderCSSTokenizerInput(reader);
  }

  static CSSTokenizerInput fromString(String text) {
    return fromReader(new StringReader(text));
  }

}
