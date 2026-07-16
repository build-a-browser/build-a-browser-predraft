package net.buildabrowser.babbrowser.html.html.imp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;

public class HTMLFormElementImp extends HTMLElementImp implements HTMLFormElement {

  private final List<WeakReference<FormAssociatedElement>> submittableElements
    = new ArrayList<>();

  private boolean constructEntryList = false;

  public HTMLFormElementImp(
    String name, String namespace, Node parentNode
  ) {
    super(name, namespace, parentNode);
  }

  @Override
  public boolean constructingEntryList() {
    return this.constructEntryList;
  }

  @Override
  public void setConstructingEntryList(boolean constructingEntryList) {
    this.constructEntryList = constructingEntryList;
  }

  @Override
  public List<FormAssociatedElement> submittableElements() {
    List<FormAssociatedElement> elementList = new ArrayList<>();
    // TODO: Sort by document tree order
    for (WeakReference<FormAssociatedElement> elementRef: submittableElements) {
      FormAssociatedElement element = elementRef.get();
      if (element == null) continue;
      elementList.add(element);
    }
    return elementList;
  }

  @Override
  public void addSubmittableElement(FormAssociatedElement element) {
    submittableElements.add(new WeakReference<>(element));
  }

  @Override
  public void removeSubmittableElement(FormAssociatedElement element) {
    submittableElements.removeIf(e -> e.get().equals(element));
  }
  
  
}
