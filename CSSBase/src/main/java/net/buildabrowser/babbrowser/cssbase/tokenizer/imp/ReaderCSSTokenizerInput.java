package net.buildabrowser.babbrowser.cssbase.tokenizer.imp;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;

public class ReaderCSSTokenizerInput implements CSSTokenizerInput {

  private final PushbackReader reader;

  public ReaderCSSTokenizerInput(Reader reader) {
    this.reader = new PushbackReader(reader, 16);
  }

  @Override
  public int read() throws IOException {
    int ch = reader.read();
    switch (ch) {
      case '\r':
        int ch2;
        if ((ch2 = reader.read()) != '\n') {
          reader.unread(ch2);
        }
        return '\n';
      case '\f':
        return '\n';
      case 0:
        return '\uFFFD';
    }

    if (Character.isSurrogate((char) ch)) {
      return '\uFFFD';
    }

    return ch;
  }

  @Override
  public void unread(int ch) throws IOException {
    if (ch == -1) return;
    reader.unread(ch);
  }

  @Override
  public int peek() throws IOException {
    int ch = read();
    unread(ch);
    return ch;
  }
  
}
