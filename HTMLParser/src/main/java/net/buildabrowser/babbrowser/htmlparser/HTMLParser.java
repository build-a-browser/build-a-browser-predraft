package net.buildabrowser.babbrowser.htmlparser;

import java.io.IOException;
import java.io.Reader;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.htmlparser.imp.HTMLParserImp;

public interface HTMLParser {
  
  MutableDocument parse(Reader streamReader, DocumentChangeListener changeListener) throws IOException;

  default MutableDocument parse(Reader streamReader) throws IOException {
    return parse(streamReader, new DocumentChangeListener() {
      @Override public void onNodeAdded(Node node) {}
      @Override public void onNodeRemoved(Node node) {}
    });
  }

  public static HTMLParser create() {
    return new HTMLParserImp();
  }

}
