package net.buildabrowser.babbrowser.renderer.content.input;

import java.util.Objects;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
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
    System.out.println("Intrinsics");
    innerContent().computeIntrinsics();
  }

  @Override
  public void computeMeasures(ElementBox box, LayoutConstraint referenceConstraint) {
    innerContent().computeMeasures(box, referenceConstraint);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    System.out.println("Layout");
    return innerContent().layout(widthConstraint, heightConstraint);
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    innerContent().positionLayers(layerX, layerY);
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
  private InputTypeContent innerContent() {
    String currentType = rootBox.element().getAttribute("type");
    if (
      inputContent != null
      && Objects.equals(lastType, currentType)
    ) return inputContent;

    if (currentType == null) {
      currentType = "text";
    }

    return inputContent = switch (currentType) {
      case "text" -> new TextTypeContent(rootBox);
      default -> new TextTypeContent(rootBox);
    };
  }
  
}
