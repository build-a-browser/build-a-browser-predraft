package net.buildabrowser.babbrowser.htmlparser.shared.imp;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Map;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.dom.algo.StyleAlgos;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.RawTextState;

public class ParseContextImp implements ParseContext {

  private final ArrayDeque<Node> nodes = new ArrayDeque<>();
  private final StringBuilder textBuffer = new StringBuilder();

  private final MutableDocument document; // TODO: Remove?
  private final TokenizeContext tokenizeContext;

  public ParseContextImp(MutableDocument document, TokenizeContext tokenizeContext) {
    this.document = document;
    this.tokenizeContext = tokenizeContext;
    nodes.push(document);
  }

  @Override
  public void emitCharacterToken(int ch) {
    textBuffer.appendCodePoint(ch);
  }

  @Override
  public void emitEOFToken() {
    closeActive();
  }

  @Override
  public void emitTagToken(TagToken tagToken) {
    if (tagToken.isStartTag()) {
      pushElement(tagToken.name(), tagToken.attributes());
      if (tagToken.name().equals("style")) {
        tokenizeContext.setTokenizeState(new RawTextState());
      }
      if (tagToken.isSelfClosing()) {
        closeActive();
      }
    } else {
      assert nodes.peek() instanceof Element: "Expected to pop element!";
      Element e = (Element) nodes.peek();
      assert e.name().equals(tagToken.name()): "Existing tag was " + e.name() + " but new tag is " + tagToken.name();
      closeActive();

      if (tagToken.name().equals("style")) {
        StyleAlgos.updateAStyleBlock(e, document);
      }
    }
  }

  private void pushElement(String name, Map<String, String> attributes) {
    Element element = Element.create(name, new LinkedList<>(), attributes);
    switch (nodes.peek()) {
      case Document document -> document.children().add(element);
      case Element e -> e.children().add(element);
      default -> throw new UnsupportedOperationException("Don't know how to push to this element!");
    }
    nodes.push(element);
  }

  private void closeActive() {
    if (!textBuffer.isEmpty()) {
      Text text = Text.create(textBuffer.toString());
      textBuffer.setLength(0);

      switch (nodes.peek()) {
        case Document document -> document.children().add(text);
        case Element element -> element.children().add(text);
        default -> throw new UnsupportedOperationException("Don't know how to push to this element!");
      }
    }
    nodes.pop();
  }
  
}
