package net.buildabrowser.babbrowser.renderer.content.input.text;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.stringRepeat;

import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.form.FormSubmissionAlgorithm;
import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;
import net.buildabrowser.babbrowser.renderer.content.common.AbstractTextController;

public class InputTextController extends AbstractTextController {

  private static final String PASSWORD_CHARACTER = "\u2219";

  private final HTMLInputElement element;
  private final boolean isHidden;

  public InputTextController(
    HTMLInputElement element,
    boolean isHidden
  ) {
    this.element = element;
    this.isHidden = isHidden;
  }

  @Override
  public String value() {
    return element.value();
  }

  @Override
  public void setValue(String value) {
    element.setValue(value);
  }

  @Override
  public String displayValue() {
    if (isHidden) {
      int valueLen = value().length();
      return stringRepeat(PASSWORD_CHARACTER, valueLen);
    } else return value();
  }

  @Override
  public void submit() {
    if (!(
      element instanceof FormAssociatedElement formAssociatedElement
    )) return;

    HTMLFormElement formOwner = formAssociatedElement.form();
    if (formOwner == null) return;

    for (FormAssociatedElement submittable:
      formOwner.submittableElements().elements()
    ) {
      if (
        !FormSubmissionAlgorithm.isSubmitButton(submittable)
      ) continue;
      EventDispatcher.dispatch(
        PointerEvent.createGeneric("click"),
        submittable);
      return;
    }

    FormSubmissionAlgorithm.submitAForm(
      formOwner, element,
      UserNavigationInvolvement.ACTIVATION);
  }
  
}
