package net.buildabrowser.babbrowser.bindings.parser;

import java.io.IOException;

import net.buildabrowser.babbrowser.bindings.parser.token.IDLToken;

public interface IDLTokenStream {
 
  IDLToken read() throws IOException;

}
