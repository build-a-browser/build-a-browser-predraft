package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;

public interface FormAssociatedElement extends HTMLElement {
  
  HTMLFormElement formOwner();

  void setFormOwner(HTMLFormElement formOwner);

  String value();

  default void resetFormOwner() {
    // TODO: Unset parser inserted flag
    // TODO: Early return
    if (formOwner() != null) {
      formOwner().removeSubmittableElement(this);
    }
    setFormOwner(null);
    // TODO: Support listed form owner
    Node ancestor = parentNode();
    while (ancestor != null) {
      if (
        ancestor instanceof HTMLFormElement formElement
      ) {
        formElement.addSubmittableElement(this);
        setFormOwner(formElement);
        return;
      }
      ancestor = ancestor.parentNode();
    }
  }

}
