package net.buildabrowser.babbrowser.htmlparser;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.htmlparser.imp.HTMLParserImp;

public interface HTMLParser {
  
  void parse(ByteBuffer buffer);

  void parse(int codepoint);

  void done();

  public static Document parse(Reader streamReader) throws IOException {
    Document document = Document.create(new DocumentChangeListener() {
      @Override public void onNodeAdded(Node node) {}
      @Override public void onNodeRemoved(Node node) {}
    });

    HTMLParser parser = create(document, StandardCharsets.UTF_8);
    int ch;
    while ((ch = streamReader.read()) != -1) {
      parser.parse(ch);
    }
    parser.done();

    return document;
  }

  // The spec has the parser create the document, but we need some parameters, so it is easier to pass in
  public static HTMLParser create(Document document, Charset charset) {
    return new HTMLParserImp(document, charset);
  }

}
