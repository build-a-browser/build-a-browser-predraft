package net.buildabrowser.babbrowser.renderer.layout.stacking;

import java.util.Deque;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;

public final class StackingContextGenerator {
  
  private StackingContextGenerator() {}

  public static void generateStackingContextsRoot(
    ElementBox rootBox, Deque<ElementBox> deferredBoxes
  ) {
    StackingContext rootContext = StackingContext.createRoot(rootBox);
    rootBox.setStackingContext(rootContext);
    rootContext.computeInsets();

    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      generateStackingContexts(childIt.next(), rootContext, deferredBoxes);
    }
  }

  public static void generateStackingContextsDeferred(
    ElementBox deferredBox, Deque<ElementBox> deferredBoxes
  ) {
    // TODO: Need to track font
    StackingContext deferredContext = deferredBox.stackingContext();
    assert deferredContext != null;

    for (Box box: deferredBox.childBoxes()) {
      generateStackingContexts(box, deferredContext, deferredBoxes);
    }
  }

  private static void generateStackingContexts(
    Box box, StackingContext parentContext, Deque<ElementBox> deferredBoxes
  ) {
    if (!(box instanceof ElementBox elementBox)) return;
    if (elementBox.parentBox() instanceof ScrollBox) {
      generateScrollChildStackingContexts(elementBox, parentContext, deferredBoxes);
      return;
    }

    CSSValue positioning = elementBox.properties().get(CSSProperty.POSITION);
    boolean isScrollable = elementBox instanceof ScrollBox;

    if (isDeferred(positioning)) {
      parentContext = parentContext.createChild(elementBox);
      deferredBoxes.add(elementBox);
    } else if (
      positioning.equals(PositionValue.RELATIVE)
      || positioning.equals(PositionValue.STICKY)
      || isScrollable
    ) {
      parentContext = parentContext.createChild(elementBox);
      parentContext.computeInsets();
    } // TODO: Other positions
    elementBox.setStackingContext(parentContext);

    ElementBoxIterator childIt = elementBox.childBoxes();
    if (!isDeferred(positioning)) {
      while (childIt.hasNext()) {
        generateStackingContexts(childIt.next(), parentContext, deferredBoxes);
      }
    }
  }

  private static boolean isDeferred(CSSValue positioning) {
    return
      positioning.equals(PositionValue.ABSOLUTE)
      || positioning.equals(PositionValue.FIXED);
  }

  private static void generateScrollChildStackingContexts(
    ElementBox elementBox, StackingContext parentContext, Deque<ElementBox> deferredBoxes
  ) {
    elementBox.setStackingContext(parentContext);
    ElementBoxIterator childIt = elementBox.childBoxes();
    while (childIt.hasNext()) {
      generateStackingContexts(childIt.next(), parentContext, deferredBoxes);
    }
  }

}
