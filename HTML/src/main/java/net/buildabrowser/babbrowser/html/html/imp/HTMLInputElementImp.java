package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.form.FormSubmissionAlgorithm;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.util.HTMLEventUtil;

public class HTMLInputElementImp extends HTMLElementImp implements HTMLInputElement, ActivationTarget {

  private String value = "";
  private HTMLFormElement formOwner;

  public HTMLInputElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  // TODO: Limit to known attributes
  // TODO: Invalidate when type attribute changes
  @Override
  public String type() {
    return getAttribute("type");
  }

  @Override
  public void setType(String type) {
    addAttribute("type", type);
  }

  @Override
  public String value() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    this.value = value == null ? "" : value;
    // TODO: There might be cases where it should invalidate layout
    // instead - should delegate to the renderer
    invalidate(InvalidationLevel.PAINT);
  }

  @Override
  public void activate(PointerEvent event) {
    if (formOwner == null) return;
    // TODO: Check if document is fully active
    if (FormSubmissionAlgorithm.isSubmitButton(this)) {
      FormSubmissionAlgorithm.submitAForm(
        formOwner, this, HTMLEventUtil.userNavigationInvolvement(event));
    }
  }

  @Override
  public HTMLFormElement formOwner() {
    return this.formOwner;
  }

  @Override
  public void setFormOwner(HTMLFormElement formOwner) {
    this.formOwner = formOwner;
  }
  
}
