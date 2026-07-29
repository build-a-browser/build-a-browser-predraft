package net.buildabrowser.babbrowser.renderer.content.textarea;

import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;
import net.buildabrowser.babbrowser.renderer.content.common.AbstractTextController;

public class TextAreaController extends AbstractTextController {

  private final HTMLTextAreaElement element;

  public TextAreaController(
    HTMLTextAreaElement element
  ) {
    this.element = element;
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
    return value();
  }

  @Override
  public void submit() {}
  
}
