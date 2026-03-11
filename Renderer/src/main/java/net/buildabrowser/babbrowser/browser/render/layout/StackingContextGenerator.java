package net.buildabrowser.babbrowser.browser.render.layout;

import java.util.Deque;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;

public final class StackingContextGenerator {
  
  private StackingContextGenerator() {}

  public static void generateStackingContextsRoot(
    ElementBox rootBox, Deque<ElementBox> deferredBoxes, GlobalLayoutContext globalLayoutContext
  ) {
    // TODO: Need to track font
    LayoutContext layoutContext = new LayoutContext(globalLayoutContext, globalLayoutContext.rootMetrics());
    StackingContext rootContext = StackingContext.createRoot(rootBox);
    rootBox.setStackingContext(rootContext);
    rootContext.computeInsets(layoutContext);

    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      generateStackingContexts(childIt.next(), rootContext, layoutContext, deferredBoxes);
    }
  }

  public static void generateStackingContextsDeferred(
    ElementBox deferredBox, Deque<ElementBox> deferredBoxes, GlobalLayoutContext globalLayoutContext
  ) {
    // TODO: Need to track font
    LayoutContext layoutContext = new LayoutContext(globalLayoutContext, globalLayoutContext.rootMetrics());
    StackingContext deferredContext = deferredBox.stackingContext();

    ElementBoxIterator childIt = deferredBox.childBoxes();
    while (childIt.hasNext()) {
      generateStackingContexts(childIt.next(), deferredContext, layoutContext, deferredBoxes);
    }
  }

  private static void generateStackingContexts(
    Box box, StackingContext parentContext, LayoutContext layoutContext,
    Deque<ElementBox> deferredBoxes
  ) {
    if (!(box instanceof ElementBox elementBox)) return;
    CSSValue positioning = elementBox.activeStyles().getProperty(CSSProperty.POSITION);
    if (positioning.equals(PositionValue.RELATIVE)) {
      parentContext = parentContext.createChild(elementBox);
      parentContext.computeInsets(layoutContext);
    } else if (positioning.equals(PositionValue.ABSOLUTE)) {
      parentContext = parentContext.createChild(elementBox);
      deferredBoxes.add(elementBox);
    } // TODO: Other positions
    elementBox.setStackingContext(parentContext);
    ElementBoxIterator childIt = elementBox.childBoxes();
    if (!positioning.equals(PositionValue.ABSOLUTE)) {
      while (childIt.hasNext()) {
        generateStackingContexts(childIt.next(), parentContext, layoutContext, deferredBoxes);
      }
    }
  }

}
