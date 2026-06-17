package net.buildabrowser.babbrowser.html.html.imp;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.imp.ElementImp;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusOptions;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public class HTMLElementImp extends ElementImp implements HTMLElement {

  private static final List<String> FOCUSABLE_ELEMENTS = List.of(
    "a", "area", "button", "frame", "iframe", "input", "object", "select", "textarea");

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

  @Override
  public long tabIndex() {
    String attribute = getAttribute("tabindex");
    if (attribute != null) {
      // TODO: Infra util for integer parsing
      Long parsedValue = CommonUtil.tryOrNull(() -> Long.valueOf(attribute));
      if (parsedValue != null) return parsedValue;
    }

    return FOCUSABLE_ELEMENTS.indexOf(name()) != -1 ? 0 : -1;
  }

  @Override
  public void focus(FocusOptions options) {
    ((HTMLDocument) nodeDocument()).focusManager().focus(this, options);
  }

}
