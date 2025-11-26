package net.buildabrowser.babbrowser.htmlparser;

import java.io.IOException;
import java.io.Reader;

import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.htmlparser.imp.HTMLParserImp;

public interface HTMLParser {
  
  MutableDocument parse(Reader streamReader) throws IOException;

  public static HTMLParser create() {
    return new HTMLParserImp();
  }

}
