package net.buildabrowser.babbrowser.render.layout;

import java.util.Deque;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerUtil;

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

    ElementBoxIterator childIt = deferredBox.childBoxes();
    while (childIt.hasNext()) {
      generateStackingContexts(childIt.next(), deferredContext, deferredBoxes);
    }
  }

  private static void generateStackingContexts(
    Box box, StackingContext parentContext, Deque<ElementBox> deferredBoxes
  ) {
    if (!(box instanceof ElementBox elementBox)) return;

    ActiveStyles activeStyles = elementBox.activeStyles();
    CSSValue positioning = activeStyles.getProperty(CSSProperty.POSITION);
    boolean isScrollable = CompositeLayerUtil.hasScrollContent(elementBox);

    if (positioning.equals(PositionValue.ABSOLUTE)) {
      parentContext = parentContext.createChild(elementBox);
      deferredBoxes.add(elementBox);
    } else if (
      positioning.equals(PositionValue.RELATIVE)
      || isScrollable
    ) {
      parentContext = parentContext.createChild(elementBox);
      parentContext.computeInsets();
    } // TODO: Other positions
    elementBox.setStackingContext(parentContext);

    ElementBoxIterator childIt = elementBox.childBoxes();
    if (!positioning.equals(PositionValue.ABSOLUTE)) {
      while (childIt.hasNext()) {
        generateStackingContexts(childIt.next(), parentContext, deferredBoxes);
      }
    }
  }

}
