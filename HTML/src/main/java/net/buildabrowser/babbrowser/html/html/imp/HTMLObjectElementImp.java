package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement;

public class HTMLObjectElementImp extends HTMLElementImp implements HTMLObjectElement {

  private static final ObjectRepresentation CHILDREN_REPRESENTATION
    = new ChildrenRepresentation();

  private ObjectRepresentation representation = CHILDREN_REPRESENTATION;

  public HTMLObjectElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public ObjectRepresentation representation() {
    return this.representation;
  }

  @Override
  public void setRepresentation(ObjectRepresentation representation) {
    if (representation == this.representation) return;
    this.representation = representation;
    invalidate(InvalidationLevel.BOX);
  }
  
}
