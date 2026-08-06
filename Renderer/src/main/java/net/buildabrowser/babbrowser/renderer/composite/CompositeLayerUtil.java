package net.buildabrowser.babbrowser.renderer.composite;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public class CompositeLayerUtil {
  
  private CompositeLayerUtil() {}

  public static boolean hasScrollContent(RenderContext renderContext) {
    PropertyContainer properties = renderContext.properties();
    CSSValue overflowX = properties.get(CSSProperty.OVERFLOW_X);
    CSSValue overflowY = properties.get(CSSProperty.OVERFLOW_Y);

    if (
      // HTML scroll is managed by the fake outer box
      renderContext instanceof ElementContext elementContext
      && isHtmlElement(elementContext.element(), "html")
    ) return false;

    return causesScrollContent(overflowX) || causesScrollContent(overflowY);
  }

  public static boolean hasScrollContent(ElementBox elementBox) {
    return
      elementBox.context() != null
      && hasScrollContent(elementBox.context())
      && elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL);
  }

  public static boolean causesScrollContent(CSSValue scrollValue) {
    // TODO: I think the element has to be a block container if it reaches here, but double check later
    return !(
      scrollValue.equals(OverflowValue.VISIBLE)
      || scrollValue.equals(OverflowValue.CLIP));
  }

  public static CSSValue adjustHTMLOverflowValue(
    RenderContext renderContext, CSSProperty relatedProperty
  ) {
    PropertyContainer properties = renderContext.properties();
    CSSValue guessedValue = properties.get(relatedProperty);
    HTMLElement element = renderContext.element();
    if (guessedValue.equals(OverflowValue.VISIBLE)) {
      // Even though there's only one <body>, the spec says to scan for a body without display: none;
      Node currentNode = element.firstChild();
      while (currentNode != null) {
        Node node = currentNode;
        currentNode = currentNode.nextSibling();
        
        if (!(node instanceof HTMLElement childElement)) continue;
        // TODO: instanceof BodyElement
        if (!childElement.name().equals("body")) continue;
        if (
          properties.get(CSSProperty.DISPLAY).equals(DisplayValue.NONE)
        ) continue;

        guessedValue = properties.get(relatedProperty);
        break;
      }
    }
    if (guessedValue.equals(OverflowValue.VISIBLE)) {
      return OverflowValue.AUTO;
    }

    return guessedValue;
  }

}
