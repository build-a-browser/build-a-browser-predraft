package net.buildabrowser.babbrowser.renderer.layout;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.FontDetermination.FontDeterminationContext;

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
      parentFontInfo, box.properties(), parentContext);
    LayoutContext childContext = parentContext;
    if (!childFontInfo.equals(parentFontInfo)) {
      LoadedFont newFont = childFontInfo.font();
      FontMetrics rootMetrics = isHtmlElement(box.element(), "html") ?
        newFont.metrics() :
        parentContext.rootMetrics();
      childContext = new LayoutContext(parentContext.global(), newFont, rootMetrics);
    }

    box.setLayoutContext(childContext);

    for (Box child: box.childBoxes()) {
      if (child instanceof ElementBox elementBox) {
        generateLayoutContexts(elementBox, childContext, childFontInfo);
      }
    }
  }

}
