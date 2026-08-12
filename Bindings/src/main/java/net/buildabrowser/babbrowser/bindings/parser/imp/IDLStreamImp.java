package net.buildabrowser.babbrowser.bindings.parser.imp;

import java.io.IOException;
import java.io.PushbackReader;

import net.buildabrowser.babbrowser.bindings.parser.IDLStream;

public class IDLStreamImp implements IDLStream {

  private final PushbackReader reader;

  public IDLStreamImp(PushbackReader reader) {
    this.reader = reader;
  }

  @Override
  public int read() throws IOException {
    return reader.read();
  }

  @Override
  public void unread(int ch) throws IOException {
    if (ch != -1) reader.unread(ch);
  }
  
}
