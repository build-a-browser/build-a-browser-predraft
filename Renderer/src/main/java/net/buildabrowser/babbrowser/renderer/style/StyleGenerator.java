package net.buildabrowser.babbrowser.renderer.style;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public final class StyleGenerator {
 
  private StyleGenerator() {}

  public static void style(
    Node node,
    StyleCache styleCache,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    style(node, styleCache, renderContexts, (ActiveStyles) null, false);
  }

  public static ActiveStyles style(
    Node node,
    StyleCache styleCache,
    SlotFamily<HTMLElement, RenderContext> renderContexts,
    ActiveStyles refStyles,
    boolean updateParentedStyles
  ) {
    boolean styleSelf = false;
    boolean styleChildren = true;
    ActiveStyles nextRef = refStyles;
    if (node instanceof HTMLElement element) {
      RenderContext renderContext = renderContexts.get(element);
      styleSelf = (renderContext.invalidationLevel() & InvalidationLevel.STYLE_SELF) != 0;
      styleChildren = (renderContext.invalidationLevel() & InvalidationLevel.STYLE) != 0;
      if (updateParentedStyles | styleSelf) {
        nextRef = renderContext.regenerateStyles(styleCache, refStyles);
      }
    }

    updateParentedStyles |= styleSelf;
    styleChildren |= updateParentedStyles;

    Node childNode = node.firstChild();
    ActiveStyles childRef = nextRef;
    if (styleChildren) while (childNode != null) {
      childRef = style(
        childNode, styleCache, renderContexts,
        childRef, updateParentedStyles);
      childNode = childNode.nextSibling();
    }

    return nextRef;
  }

}
