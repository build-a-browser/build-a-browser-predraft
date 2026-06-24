package net.buildabrowser.babbrowser.renderer.content.input;

import java.util.Objects;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.button.ButtonTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.hidden.HiddenTypeContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class InputContent implements BoxContent {

  private ElementBox rootBox;
  private String lastType;
  private InputTypeContent inputContent;

  public InputContent(ElementBox rootBox) {
    this.rootBox = rootBox;
  }
  
  @Override
  public void fixupChildren() {
    innerContent().fixupChildren();
  }

  @Override
  public void computeIntrinsics() {
    innerContent().computeIntrinsics();
  }

  @Override
  public void computeMeasures(ElementBox box, LayoutConstraint referenceConstraint) {
    innerContent().computeMeasures(box, referenceConstraint);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    return innerContent().layout(widthConstraint, heightConstraint);
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    innerContent().positionLayers(layerX, layerY);
  }

  @Override
  public <T extends BoxContent> EventHandlerResponse withFocusEventHandler(
    FocusEventHandlerFunc<T> withHandlerFunc
  ) {
    return innerContent().withFocusEventHandler(withHandlerFunc);
  }

  @Override
  public boolean isReplaced() {
    return innerContent().isReplaced();
  }

  @Override
  public boolean hasCustomContent() {
    return true;
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }

  // The input type should not change between the calls of the above methods
  // in the same layout cycle
  // Because layout should never occur at the same time as another task that can
  // change attributes
  @SuppressWarnings("unchecked")
  public <T extends InputTypeContent> T innerContent() {
    String currentType = ((HTMLInputElement) rootBox.element()).type();
    if (currentType == null) {
      currentType = "text";
    }

    if (
      inputContent != null
      && Objects.equals(lastType, currentType)
    ) return (T) inputContent;

    lastType = currentType;
    return (T) (inputContent = switch (currentType) {
      case "hidden" -> new HiddenTypeContent(rootBox);
      case "text" -> new TextTypeContent(rootBox, false);
      case "password" -> new TextTypeContent(rootBox, true);
      case "submit" -> new ButtonTypeContent(rootBox, "Submit");
      case "button" -> new ButtonTypeContent(rootBox, "");
      default -> new TextTypeContent(rootBox, false);
    });
  }
  
}
