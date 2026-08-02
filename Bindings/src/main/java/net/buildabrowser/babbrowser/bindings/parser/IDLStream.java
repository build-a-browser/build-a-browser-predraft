package net.buildabrowser.babbrowser.bindings.parser;

import java.io.IOException;

public interface IDLStream {
  
  int read() throws IOException;

  void unread(int ch) throws IOException;

  default int peek() throws IOException {
    int ch = read();
    unread(ch);
    return ch;
  }

}
