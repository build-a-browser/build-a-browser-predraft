package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;

public final class LayoutContextGenerator {
  
  private LayoutContextGenerator() {}

  public static void generateLayoutContexts(ElementBox box, LayoutContext parentContext) {
    // What a thrilling implementation...
    // TODO: Expand this more layer
    box.setLayoutContext(parentContext);

    ElementBoxIterator childIt = box.childBoxes();
    while (childIt.hasNext()) {
      if (childIt.next() instanceof ElementBox elementBox) {
        generateLayoutContexts(elementBox, parentContext);
      }
    }
  }

}
