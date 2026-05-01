package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSetListener;
import net.buildabrowser.babbrowser.dom.Element;

public class ElementIdTreeListener implements ElementSetListener {

  private final ElementSet allElements;

  private int[] elParents;
  private ElementSetListener nextListener;

  public ElementIdTreeListener(ElementSet allElements) {
    this.allElements = allElements;
  }

  public int getElementParentId(int elementId) {
    return elParents[elementId];
  }

  @Override
  public void onResize(int size) {
    if (this.elParents == null) {
      this.elParents = new int[size];
    }

    int[] oldElParents = elParents;
    int[] newElParents = new int[size];
    System.arraycopy(
      oldElParents, 0, newElParents, 0,
      Math.min(oldElParents.length, newElParents.length));
    this.elParents = newElParents;
    // Not going to bother filling the rest of the array with -1, if an element is
    // queried, it should have been added by onElementAdd which sets -1
  }

  @Override
  public void onElementAdded(Element element) {
    if (element.parentNode() instanceof Element parentElement) {
      elParents[element.getId()] = parentElement.getId();
    } else {
      elParents[element.getId()] = -1;
    }
  }

  @Override
  public void onElementRemoved(Element element) {
    // Presumably the element and its children won't match any combinators now
    // and thus the ID tree will not be queried for that element
  }

  // TODO: Also need an event when element changes parent

  @Override
  public ElementSetListener next() {
    return this.nextListener;
  }

  @Override
  public void setNext(ElementSetListener nextListener) {
    this.nextListener = nextListener;
  }

  public void resync() {
    for (Element element: allElements) {
      onElementAdded(element);
    }
  }
  
}
