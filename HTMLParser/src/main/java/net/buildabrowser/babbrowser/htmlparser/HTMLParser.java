package net.buildabrowser.babbrowser.htmlparser;

import java.io.IOException;
import java.io.Reader;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.htmlparser.imp.HTMLParserImp;

public interface HTMLParser {
  
  // // The spec has the parser create the document, but we need some parameters, so it is easier to pass in
  MutableDocument parse(Reader streamReader, MutableDocument document) throws IOException;

  default MutableDocument parse(Reader streamReader) throws IOException {
    return parse(streamReader, MutableDocument.create(new DocumentChangeListener() {
      @Override public void onNodeAdded(Node node) {}
      @Override public void onNodeRemoved(Node node) {}
    }, null));
  }

  public static HTMLParser create() {
    return new HTMLParserImp();
  }

}
