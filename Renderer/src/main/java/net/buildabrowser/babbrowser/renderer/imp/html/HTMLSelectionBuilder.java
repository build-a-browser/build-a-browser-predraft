package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.html.selection.SelectionUtil.SelectionUtilCallbacks;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;

public class HTMLSelectionBuilder implements SelectionUtilCallbacks {

  private final SelectionContext selectionContext;
  private final Node rootNode;

  private Node currentNode;

  public HTMLSelectionBuilder(
    Selection selection,
    SelectionContext selectionContext
  ) {
    this.selectionContext = selectionContext;
    // TODO: Create a wrapper node with styling data if needed
    this.rootNode = Document.create();
    this.currentNode = rootNode;
    createWrapperNodeIfNeeded(selection);
  }

  @Override
  public void onNodeSelected(Node node) {
    switch (node) {
      case Text text -> onTextSelected(text);
      default -> {}
    }
  }

  int i = 0;
  @Override
  public void onNodeEntered(Node node) {
    switch (node) {
      case Element element -> enterElement(element);
      default -> {}
    }
  }

  @Override
  public void onNodeExited(Node node) {
    switch (node) {
      case Element element -> this.currentNode = currentNode.parentNode(); 
      default -> {}
    }
  }

  public Node rootNode() {
    return this.rootNode;
  }

  private void onTextSelected(Text text) {
    String textStr = text.data().substring(
      (int) selectionContext.selectionStart(text),
      (int) selectionContext.selectionEnd(text));
    currentNode.appendChild(Text.create(textStr));
  }

  private void enterElement(Element element) {
    Element refElement = Element.create(
      element.name(), element.namespace(), currentNode);
    for (String attrName: element.getAttributeNames()) {
      refElement.addAttribute(attrName, element.getAttribute(attrName));
    }
    // TODO: Also override style attribute with all styling data
    currentNode.appendChild(refElement);
    this.currentNode = refElement;
  }

  private void createWrapperNodeIfNeeded(Selection selection) {
    Node anchorNode = selection.anchorNode();
    boolean needsWrapperNode =
      anchorNode instanceof Text
      && anchorNode == selection.focusNode();
    if (!needsWrapperNode) return;
    if (!(
      anchorNode.parentNode() instanceof Element parentEl
    )) return;
    
    enterElement(parentEl);
  }
        
}
