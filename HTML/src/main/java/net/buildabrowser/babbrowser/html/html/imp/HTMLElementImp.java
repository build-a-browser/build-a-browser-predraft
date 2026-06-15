package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
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

  private SlotItem<?> slotItems;
 
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

  private void invalidate(InvalidationLevel invalidationLevel) {
    // For some reason IntrusiveList#forEach does not work here
    SlotItem<?> currentItem = slotItems;
    while (currentItem != null) {
      if (currentItem instanceof Invalidatable invalidatable) {
        invalidatable.invalidate(invalidationLevel);
      }
      currentItem = currentItem.next();
    }
  }

  @Override
  public void setSlots(SlotItem<?> slotItem) {
    this.slotItems = slotItem;
  }

  @Override
  public SlotItem<?> slots() {
    return this.slotItems;
  }

}
