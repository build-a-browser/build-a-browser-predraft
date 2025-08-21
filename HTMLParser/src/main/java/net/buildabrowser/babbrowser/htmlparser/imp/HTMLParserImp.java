package net.buildabrowser.babbrowser.htmlparser.imp;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.htmlparser.HTMLParser;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.spec.dom.Document;

public class HTMLParserImp implements HTMLParser {

  private static final int EOF = -1;
  
  public Document parse(Reader streamReader) throws IOException {
    PushbackReader pushbackReader = new PushbackReader(streamReader, 2);
    TokenizeContext tokenizeContext = TokenizeContext.create(pushbackReader);
    Document document = new Document(new LinkedList<>());
    ParseContext parseContext = ParseContext.create(document);

    int ch = 0;
    while ((ch = pushbackReader.read()) != EOF) {
      tokenizeContext.getTokenizeState().consume(ch, tokenizeContext, parseContext);
    }
    tokenizeContext.getTokenizeState().consume(EOF, tokenizeContext, parseContext);

    return document;
  };

}
