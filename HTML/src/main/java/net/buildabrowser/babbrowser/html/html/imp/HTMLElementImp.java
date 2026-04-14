package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.imp.ElementImp;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public class HTMLElementImp extends ElementImp implements HTMLElement {

  private InvalidationLevel invalidationLevel = InvalidationLevel.NONE;
  private Object context;
  private Object box;
 
  public HTMLElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public Node appendChild(Node node) {
    super.appendChild(node);

    if (node instanceof Invalidatable invalidatable) {
      invalidatable.invalidate(InvalidationLevel.BOX);
    }
    invalidate(InvalidationLevel.BOX);

    return node;
  }

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    if (invalidationLevel.ordinal() < this.invalidationLevel.ordinal()) {
      this.invalidationLevel = invalidationLevel;
      if (parentNode() instanceof Invalidatable parentInvalidatable) {
        parentInvalidatable.invalidate(invalidationLevel);
      }
    }
  }

  @Override
  public void validate() {
    if (this.invalidationLevel == InvalidationLevel.NONE) return;
    
    this.invalidationLevel = InvalidationLevel.NONE;
    Node currentNode = firstChild();
    while (currentNode != null) {
      if (currentNode instanceof Invalidatable invalidatable) {
        invalidatable.validate();
      }
      currentNode = currentNode.nextSibling();
    }
  }

  @Override
  public InvalidationLevel invalidationLevel() {
    return this.invalidationLevel;
  }

  @Override
  public Object getContext() {
    return this.context;
  }

  @Override
  public void setContext(Object context) {
    this.context = context;
  }
  
  @Override
  public Object getBox() {
    return this.box;
  }

  @Override
  public void setBox(Object box) {
    this.box = box;
  }

  @Override
  public Navigable nodeNavigable() {
    Document document = nodeDocument();
    if (
      document == null
      || !(document instanceof HTMLDocument htmlDocument)
    ) return null;
    WindowEventLoop eventLoop = htmlDocument.browsingContext().activeWindow()
      .agent().eventLoop();
    return eventLoop.getNavigable(htmlDocument);
  }

}
