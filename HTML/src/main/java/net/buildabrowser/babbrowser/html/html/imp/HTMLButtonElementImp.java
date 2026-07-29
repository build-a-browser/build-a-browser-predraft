package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.form.FormSubmissionAlgorithm;
import net.buildabrowser.babbrowser.html.html.HTMLButtonElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.util.HTMLEventUtil;

public class HTMLButtonElementImp extends HTMLElementImp implements HTMLButtonElement, ActivationTarget {

  private HTMLFormElement formOwner;

  public HTMLButtonElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public boolean disabled() {
    return hasAttribute("disabled");
  }

  @Override
  public void activate(PointerEvent event) {
    // TODO: Proper way to block event
    if (disabled()) return;
    if (formOwner == null) return;
    // TODO: Check if document is fully active
    if (FormSubmissionAlgorithm.isSubmitButton(this)) {
      FormSubmissionAlgorithm.submitAForm(
        formOwner, this, HTMLEventUtil.userNavigationInvolvement(event));
    }
  }

  @Override
  public boolean canBeActivated() {
    return
      formOwner != null
      && FormSubmissionAlgorithm.isSubmitButton(this);
  }

  @Override
  public String value() {
    return "";
  }

  @Override
  public HTMLFormElement form() {
    return this.formOwner;
  }

  @Override
  public void setFormOwner(HTMLFormElement formOwner) {
    this.formOwner = formOwner;
  }
  
}
