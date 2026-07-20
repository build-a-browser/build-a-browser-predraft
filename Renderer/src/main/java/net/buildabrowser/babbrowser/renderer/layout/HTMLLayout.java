package net.buildabrowser.babbrowser.renderer.layout;

import java.util.ArrayDeque;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public final class HTMLLayout {
  
  private HTMLLayout() {}

  public static void doLayout(
    ElementBox rootBox, float width, float height
  ) {
    ArrayDeque<ElementBox> deferredLayout = new ArrayDeque<>();

    UnmanagedBoxFragment<?> fragment = rootBox.layout(
      LayoutConstraint.of(width),
      LayoutConstraint.of(height));
    fragment.setPos(0, 0);

    StackingContextGenerator.generateStackingContextsRoot(rootBox, deferredLayout);
    rootBox.stackingContext().positionFragment(
      0, 0, fragment,
      rootBox.content()::positionLayers);
    
    while (!deferredLayout.isEmpty()) {
      ElementBox itemBox = deferredLayout.pop();
      switch (itemBox.properties().get(CSSProperty.POSITION)) {
        case PositionValue.FIXED -> layoutFixed(deferredLayout, itemBox);
        default -> layoutAbsolute(deferredLayout, itemBox);
      }
    }
  }

  private static void layoutAbsolute(
    ArrayDeque<ElementBox> deferredLayout,
    ElementBox itemBox
  ) {
    StackingContext ownContext = itemBox.stackingContext();
    float[] insets = ownContext.computeInsets();
    UnmanagedBoxFragment<?> itemFragment = PositionLayout.actuallyLayoutAbsolute(
      itemBox, insets);
    float[] position = PositionLayout.positionAbsolute(insets, itemFragment);
    ownContext.setAbsolutePosition(position);
    
    StackingContextGenerator.generateStackingContextsDeferred(itemBox, deferredLayout);
    ownContext.positionFragment(
      0, 0, itemFragment, itemBox.content()::positionLayers);
  }

  // TODO: Handle cases where fixed is not relative to the viewport
  private static void layoutFixed(
    ArrayDeque<ElementBox> deferredLayout,
    ElementBox itemBox
  ) {
    StackingContext ownContext = itemBox.stackingContext();
    float[] insets = ownContext.computeInsets();
    UnmanagedBoxFragment<?> itemFragment = PositionLayout.actuallyLayoutAbsolute(
      itemBox, insets);
    float[] position = PositionLayout.positionAbsolute(insets, itemFragment);
    ownContext.setAbsolutePosition(position);
    
    StackingContextGenerator.generateStackingContextsDeferred(itemBox, deferredLayout);
    ownContext.positionFragment(
      0, 0, itemFragment,
      itemBox.content()::positionLayers);
  }

}
