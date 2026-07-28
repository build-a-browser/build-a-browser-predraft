package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.form.FormSubmissionAlgorithm;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.html.util.HTMLInputUtil;
import net.buildabrowser.babbrowser.html.util.HTMLEventUtil;

public class HTMLInputElementImp extends HTMLElementImp implements HTMLInputElement, ActivationTarget {

  private boolean checked = false;
  private String value = "";
  private HTMLFormElement formOwner;

  public HTMLInputElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public boolean checked() {
    return this.checked;
  }

  @Override
  public void setChecked(boolean checked) {
    // TODO: Check disabled, update radio box
    setCheckedRaw(checked);
    if (type().equals("radio")) {
      HTMLInputUtil.deselectOtherRadioElements(this);
    }

    invalidate(InvalidationLevel.PAINT);
  }

  @Override
  public void setCheckedRaw(boolean checked) {
    // TODO: Check disabled, update radio box
    this.checked = checked;
  }

  @Override
  public boolean disabled() {
    return hasAttribute("disabled");
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
    invalidate(InvalidationLevel.LAYOUT);
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
    // TODO: Proper way to block event
    if (disabled()) return;
    // TODO: Check if document is fully active
    if (
      formOwner != null
      && FormSubmissionAlgorithm.isSubmitButton(this)
    ) {
      FormSubmissionAlgorithm.submitAForm(
        formOwner, this, HTMLEventUtil.userNavigationInvolvement(event));
    } else if ("checkbox".equals(type())) {
      // TODO: Spec says to fire events
      setChecked(!checked());
    } else if ("radio".equals(type())) {
      setChecked(true);
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
