package net.buildabrowser.babbrowser.renderer.content.input.text;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.stringRepeat;

import java.util.List;

import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.form.FormSubmissionAlgorithm;
import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.input.imp.AbstractTextController;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;

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
    setValue(element.value());
  }

  @Override
  public String lineValue(int lineNum) {
    assert lineNum == 0;
    return value();
  }

  @Override
  public List<String> displayLines() {
    if (isHidden) {
      int valueLen = element.value().length();
      return List.of(stringRepeat(PASSWORD_CHARACTER, valueLen));
    } else {
      return List.of(element.value());
    }
  }

  @Override
  public boolean isMultiLine() {
    return false;
  }
  
  @Override
  public boolean isLineContinuation(int lineNum) {
    return false;
  }

  @Override
	public void scrollToCursor(
    float contentWidth,
    float contentHeight
  ) {
    FontMetrics fontMetrics = metrics();
    String value = value();
    float adjustedWidth = Math.max(0, contentWidth - TextEditPainter.HORIZONTAL_PADDING);
    float scrollX = scrollX();
    float valueWidth = fontMetrics.stringWidth(value);
    float letterWidth = cursorX() == value.length() ?
      fontMetrics.stringWidth(TextTypeContent.PLACEHOLDER_CHARACTER) :
      fontMetrics.stringWidth(value.substring(
        cursorX(), cursorX() + 1));
    float toCursorWidth = fontMetrics.stringWidth(
      value.substring(0, cursorX()));
    float lowerBound = Math.max(0, toCursorWidth + letterWidth - adjustedWidth);
    float upperBound = toCursorWidth - letterWidth;
    if (scrollX > upperBound) {
      scrollX = upperBound;
    }
    if (scrollX < lowerBound) {
      scrollX = lowerBound;
    }
    scrollX = Math.max(0, Math.min(scrollX, valueWidth - adjustedWidth + letterWidth));
    setScrollX(scrollX);
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

  @Override
  protected void afterValueUpdate() {
    element.setValue(value());
  }
  
}
