package net.buildabrowser.babbrowser.browser.parser.imp;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;

import net.buildabrowser.babbrowser.browser.dom.Document;
import net.buildabrowser.babbrowser.browser.dom.Element;
import net.buildabrowser.babbrowser.browser.dom.Node;
import net.buildabrowser.babbrowser.browser.dom.Text;
import net.buildabrowser.babbrowser.browser.parser.HTMLParser;

public class HTMLParserImp implements HTMLParser {

  private static final int EOF = -1;
  
  public Document parse(Reader streamReader) throws IOException {
    ParseContext parseContext = new ParseContext(new ArrayDeque<>(), new StringBuilder());
    TokenizeState activeState = TokenizeState.DEFAULT;

    Document document = new Document(new ArrayList<>());
    parseContext.stack().push(document);

    int ch = 0;
    while ((ch = streamReader.read()) != EOF) {
      activeState = handleCharacter(ch, parseContext, activeState);
    }
    activeState = handleCharacter(EOF, parseContext, activeState);

    return document;
  };

  private TokenizeState handleCharacter(int ch, ParseContext context, TokenizeState activeState) {
    return switch (activeState) {
      case DEFAULT -> parseDefault(ch, context);
      case PARSE_TEXT -> parseText(ch, context);
      case PARSE_TAG -> parseTag(ch, context);
      case PARSE_OPEN_TAG -> parseOpenTag(ch, context);
      case PARSE_CLOSE_TAG -> parseCloseTag(ch, context);
      default -> throw new IllegalArgumentException("Unsupported Tokenize State!");
    };
  }

  private TokenizeState parseDefault(int ch, ParseContext context) {
    return switch (ch) {
      case EOF -> TokenizeState.DEFAULT;
      case '<' -> TokenizeState.PARSE_TAG;
      default -> parseText(ch, context);
    };
  }

  private TokenizeState parseText(int ch, ParseContext context) {
    switch (ch) {
      case EOF, '<':
        Text textNode = new Text(context.textBuilder().toString());
        context.textBuilder().setLength(0);
        addChild(context.stack.peek(), textNode);
        return parseDefault(ch, context);
      default:
        context.textBuilder.appendCodePoint(ch);
        return TokenizeState.PARSE_TEXT;
    }
  }

  private TokenizeState parseTag(int ch, ParseContext context) {
    return switch(ch) {
      case '/' -> TokenizeState.PARSE_CLOSE_TAG;
      default -> parseOpenTag(ch, context);
    };
  }

  private TokenizeState parseOpenTag(int ch, ParseContext context) {
    switch (ch) {
      case EOF:
        return TokenizeState.DEFAULT;
      case '>':
        Element element = new Element(context.textBuilder().toString(), new ArrayList<>());
        context.textBuilder().setLength(0);
        addChild(context.stack.peek(), element);
        context.stack.push(element);
        return TokenizeState.DEFAULT;
      default:
        context.textBuilder().appendCodePoint(ch);
        return TokenizeState.PARSE_OPEN_TAG;
    }
  }

  private TokenizeState parseCloseTag(int ch, ParseContext context) {
    switch (ch) {
      case EOF:
        return TokenizeState.DEFAULT;
      case '>':
        context.textBuilder().setLength(0);
        context.stack.pop();
        return TokenizeState.DEFAULT;
      default:
        context.textBuilder().appendCodePoint(ch);
        return TokenizeState.PARSE_CLOSE_TAG;
    }
  }

  private void addChild(Node parent, Node child) {
    if (parent instanceof Document document) {
      document.children().add(child);
    } else if (parent instanceof Element element) {
      element.children().add(child);
    }
  }

  private static enum TokenizeState {
    DEFAULT, PARSE_TEXT, PARSE_TAG, PARSE_OPEN_TAG, PARSE_CLOSE_TAG
  }

  private static record ParseContext(ArrayDeque<Node> stack, StringBuilder textBuilder) {

  }

}
