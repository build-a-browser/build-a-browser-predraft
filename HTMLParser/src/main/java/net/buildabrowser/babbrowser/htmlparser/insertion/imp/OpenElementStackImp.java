package net.buildabrowser.babbrowser.htmlparser.insertion.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getFirst;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.StyleAlgos;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;

public class OpenElementStackImp implements OpenElementStack {

  private final List<Node> stack = new LinkedList<>();

  @Override
  public void pushNode(Node node) {
    stack.add(0, node);
  }

  @Override
  public Node peek() {
    return getFirst(stack);
  }

  @Override
  public Node peek(int pos) {
    return stack.get(pos);
  }

  @Override
  public Node popNode() {
    Node node = stack.remove(0);
    if (ParseElementUtil.isHTMLElementWithName(node, "style")) {
      updateAStyleBlock((Element) node);
    } else if (ParseElementUtil.isHTMLElementWithName(node, "title")) {
      emitTitleElement(node);
    }
    return node;
  }

  @Override
  public void removeSpecificNode(Node node) {
    stack.remove(node);
  }

  @Override
  public int size() {
    return stack.size();
  }

  @Override
  public Iterator<Node> iterator() {
    return stack.iterator();
  }

  // TODO: Not really the best place to do this
  private void emitTitleElement(Node node) {
    if (!(node.nodeDocument() instanceof HTMLDocument document)) return;
    document.setTitleElement((HTMLElement) node);
    DocumentRendererEventListener eventListener = document
      .uaNavigableOptions().eventListener();
    if (eventListener != null) {
      eventListener.onTitleChanged(document.title());
    }
  }

  private void updateAStyleBlock(Element node) {
    // Testing passes a Document instead of HTMLDocument
    Document nodeDocument = node.nodeDocument();
    URI refURL = nodeDocument instanceof HTMLDocument htmlDocument ?
      htmlDocument.baseURL() : nodeDocument.url();
    StyleAlgos.updateAStyleBlock(node, refURL);
  }
  
}
