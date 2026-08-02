package net.buildabrowser.babbrowser.bindings.parser.imp;

import net.buildabrowser.babbrowser.bindings.parser.IDLStream;
import net.buildabrowser.babbrowser.bindings.parser.IDLTokenStream;
import net.buildabrowser.babbrowser.bindings.parser.IDLTokenizer;
import net.buildabrowser.babbrowser.bindings.parser.token.IDLToken;

public class IDLTokenizerImp implements IDLTokenizer {

  @Override
  public IDLTokenStream tokenStream(IDLStream stream) {
    return () -> new IDLToken() {};
  }

}
