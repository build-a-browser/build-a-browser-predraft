package net.buildabrowser.babbrowser.htmlparser.insertion.imp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
    stack.addFirst(node);
  }

  @Override
  public Node peek() {
    return stack.getFirst();
  }

  @Override
  public Node peek(int pos) {
    return stack.get(pos);
  }

  @Override
  public Node popNode() {
    Node node = stack.removeFirst();
    if (ParseElementUtil.isHTMLElementWithName(node, "style")) {
      StyleAlgos.updateAStyleBlock((Element) node);
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
    DocumentRendererEventListener eventListener = document.renderer().eventListener();
    if (eventListener != null) {
      eventListener.onTitleChanged(document.title());
    }
  }
  
}
