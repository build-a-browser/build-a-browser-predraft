package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.browser.render.layout.FontDetermination.FontDeterminationContext;

public final class LayoutContextGenerator {
  
  private LayoutContextGenerator() {}

  public static void generateLayoutContexts(
    ElementBox box, LayoutContext parentContext
  ) {
    generateLayoutContexts(
      box, parentContext,
      new FontDeterminationContext(null, 16, 400, null));
  }

  static void generateLayoutContexts(
    ElementBox box, LayoutContext parentContext, FontDeterminationContext parentFontInfo
  ) {
    FontDeterminationContext childFontInfo = FontDetermination.determineFont(
      parentFontInfo, box.activeStyles(), parentContext);
    LayoutContext childContext = parentContext;
    if (!childFontInfo.equals(parentFontInfo)) {
      childContext = new LayoutContext(parentContext.global(), childFontInfo.font());
    }

    box.setLayoutContext(childContext);

    ElementBoxIterator childIt = box.childBoxes();
    while (childIt.hasNext()) {
      if (childIt.next() instanceof ElementBox elementBox) {
        generateLayoutContexts(elementBox, childContext, childFontInfo);
      }
    }
  }

}
