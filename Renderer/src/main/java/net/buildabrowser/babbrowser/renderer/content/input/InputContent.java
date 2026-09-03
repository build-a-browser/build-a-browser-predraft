package net.buildabrowser.babbrowser.renderer.content.input;

import java.util.Objects;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.button.ButtonTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.checkbox.CheckBoxTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.checkbox.RadioBoxTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.hidden.HiddenTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class InputContent implements BoxContent {

  private String lastType;
  private InputTypeContent inputContent;
  
  @Override
  public void fixupChildren(ElementBox box) {
    innerContent(box).fixupChildren(box);
  }

  @Override
  public void computeIntrinsics(ElementBox box) {
    innerContent(box).computeIntrinsics(box);
  }

  @Override
  public void computeMeasures(
    ElementBox box,
    LayoutConstraint referenceConstraint
  ) {
    innerContent(box).computeMeasures(box, referenceConstraint);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    UnmanagedBoxFragment<?> fragment = innerContent(box).layout(box, widthConstraint, heightConstraint);
    box.updatePositioningFragment(fragment);
    return fragment;
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    innerContent(fragment.box()).positionLayers(fragment, layerX, layerY);
  }

  @Override
  public <T extends BoxContent> EventHandlerResponse withContentEventHandler(
    ElementBox box,
    ContentEventHandlerFunc<T> withHandlerFunc
  ) {
    return innerContent(box).withContentEventHandler(box, withHandlerFunc);
  }

  @Override
  public boolean isReplaced(ElementBox box) {
    return innerContent(box).isReplaced(box);
  }

  @Override
  public boolean hasCustomContent(ElementBox box) {
    return true;
  }

  // The input type should not change between the calls of the above methods
  // in the same layout cycle
  // Because layout should never occur at the same time as another task that can
  // change attributes
  @SuppressWarnings("unchecked")
  public <T extends InputTypeContent> T innerContent(ElementBox rootBox) {
    HTMLInputElement element = (HTMLInputElement) rootBox.element();
    String currentType = element.type();
    if (currentType == null) {
      currentType = "text";
    }

    if (
      inputContent != null
      && Objects.equals(lastType, currentType)
    ) return (T) inputContent;

    lastType = currentType;
    return (T) (inputContent = switch (currentType) {
      case "hidden" -> new HiddenTypeContent();
      case "text" -> new TextTypeContent(element, false);
      case "password" -> new TextTypeContent(element, true);
      case "submit" -> new ButtonTypeContent("Submit");
      case "button" -> new ButtonTypeContent("");
      case "checkbox" -> new CheckBoxTypeContent();
      case "radio" -> new RadioBoxTypeContent();
      default -> new TextTypeContent(element, false);
    });
  }
  
}
