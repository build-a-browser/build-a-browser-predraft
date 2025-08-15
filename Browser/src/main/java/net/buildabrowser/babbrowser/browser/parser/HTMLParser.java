package net.buildabrowser.babbrowser.browser.parser;

import java.io.IOException;
import java.io.Reader;

import net.buildabrowser.babbrowser.browser.dom.Document;
import net.buildabrowser.babbrowser.browser.parser.imp.HTMLParserImp;

public interface HTMLParser {
  
  Document parse(Reader streamReader) throws IOException;

  public static HTMLParser create() {
    return new HTMLParserImp();
  }

}
