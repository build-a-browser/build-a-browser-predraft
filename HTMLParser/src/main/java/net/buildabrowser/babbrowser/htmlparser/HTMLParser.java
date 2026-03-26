package net.buildabrowser.babbrowser.htmlparser;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.htmlparser.imp.HTMLParserImp;

public interface HTMLParser {
  
  void parse(ByteBuffer buffer);

  void parse(int codepoint);

  void done();

  public static MutableDocument parse(Reader streamReader) throws IOException {
    MutableDocument document = MutableDocument.create(new DocumentChangeListener() {
      @Override public void onNodeAdded(Node node) {}
      @Override public void onNodeRemoved(Node node) {}
    }, null);

    HTMLParser parser = create(document, StandardCharsets.UTF_8);
    int ch;
    while ((ch = streamReader.read()) != -1) {
      parser.parse(ch);
    }
    parser.done();

    return document;
  }

  // The spec has the parser create the document, but we need some parameters, so it is easier to pass in
  public static HTMLParser create(MutableDocument document, Charset charset) {
    return new HTMLParserImp(document, charset);
  }

}
