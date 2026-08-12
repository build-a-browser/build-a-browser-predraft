package net.buildabrowser.babbrowser.bindings.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import net.buildabrowser.babbrowser.bindings.parser.imp.IDLStreamImp;

public interface IDLStream {
  
  int read() throws IOException;

  void unread(int ch) throws IOException;

  default int peek() throws IOException {
    int ch = read();
    unread(ch);
    return ch;
  }

  static IDLStream create(String content) {
    return new IDLStreamImp(new PushbackReader(
      new StringReader(content), 4));
  }

  static IDLStream create(Reader reader) {
    return new IDLStreamImp(new PushbackReader(reader, 4));
  }

}
