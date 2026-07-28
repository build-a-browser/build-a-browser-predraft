package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;

public interface FormAssociatedElement extends HTMLElement {
  
  HTMLFormElement formOwner();

  void setFormOwner(HTMLFormElement formOwner);

  String value();

  boolean disabled();

  default void resetFormOwner() {
    // TODO: Unset parser inserted flag
    // TODO: Early return
    if (formOwner() != null) {
      formOwner().submittableElements().removeElement(this);
    }
    setFormOwner(null);
    ((HTMLDocument) nodeDocument()).unownedSubmittableElements().addElement(this);
    // TODO: Support listed form owner
    Node ancestor = parentNode();
    while (ancestor != null) {
      if (
        ancestor instanceof HTMLFormElement formElement
      ) {
        formElement.submittableElements().addElement(this);
        setFormOwner(formElement);
        ((HTMLDocument) nodeDocument()).unownedSubmittableElements().removeElement(this);
        return;
      }
      ancestor = ancestor.parentNode();
    }
  }

}
