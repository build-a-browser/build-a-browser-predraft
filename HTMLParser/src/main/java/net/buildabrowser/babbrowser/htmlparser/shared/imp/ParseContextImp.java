package net.buildabrowser.babbrowser.htmlparser.shared.imp;

import java.util.ArrayDeque;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.spec.dom.Document;
import net.buildabrowser.babbrowser.spec.dom.Element;
import net.buildabrowser.babbrowser.spec.dom.Node;
import net.buildabrowser.babbrowser.spec.dom.Text;

public class ParseContextImp implements ParseContext {

  private final ArrayDeque<Node> nodes = new ArrayDeque<>();
  private final StringBuilder textBuffer = new StringBuilder();

  public ParseContextImp(Document document) {
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
      pushElement(tagToken.name());
    } else {
      assert nodes.peek() instanceof Element: "Expected to pop element!";
      Element e = (Element) nodes.peek();
      assert e.name().equals(tagToken.name()): "Existing tag was " + e.name() + " but new tag is " + tagToken.name();
      closeActive();
    }
  }

  private void pushElement(String name) {
    Element element = new Element(name, new LinkedList<>());
    switch (nodes.peek()) {
      case Document document -> document.children().add(element);
      case Element e -> e.children().add(element);
      default -> throw new UnsupportedOperationException("Don't know how to push to this element!");
    }
    nodes.push(element);
  }

  private void closeActive() {
    if (!textBuffer.isEmpty()) {
      Text text = new Text(textBuffer.toString());
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
