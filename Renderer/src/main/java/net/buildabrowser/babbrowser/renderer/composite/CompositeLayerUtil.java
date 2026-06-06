package net.buildabrowser.babbrowser.renderer.composite;

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

public class CompositeLayerUtil {
  
  private CompositeLayerUtil() {}

  public static boolean hasScrollContent(HTMLElement element) {
    PropertyContainer properties = ((ElementContext) element.getContext()).properties();
    CSSValue overflowX = properties.get(CSSProperty.OVERFLOW_X);
    CSSValue overflowY = properties.get(CSSProperty.OVERFLOW_Y);

    overflowX = adjustOverflowValueIfHTML(element, overflowX, CSSProperty.OVERFLOW_X);
    overflowY = adjustOverflowValueIfHTML(element, overflowY, CSSProperty.OVERFLOW_Y);

    return causesScrollContent(overflowX) || causesScrollContent(overflowY);
  }

  public static boolean hasScrollContent(ElementBox elementBox) {
    return
      elementBox.element() != null
      && hasScrollContent(elementBox.element())
      && elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL);
  }

  public static boolean causesScrollContent(CSSValue scrollValue) {
    // TODO: I think the element has to be a block container if it reaches here, but double check later
    return !(
      scrollValue.equals(OverflowValue.VISIBLE)
      || scrollValue.equals(OverflowValue.CLIP));
  }

  public static CSSValue adjustOverflowValueIfHTML(
    HTMLElement element, CSSValue guessedValue, CSSProperty relatedProperty
  ) {
    // TODO: instanceof HTMLHtmlElement
    if (
      element == null
      ||!element.name().equals("html")
    ) {
      return guessedValue;
    }

    if (guessedValue.equals(OverflowValue.VISIBLE)) {
      // Even though there's only one <body>, the spec says to scan for a body without display: none;
      for (Node node: element.childNodes()) {
        if (!(node instanceof HTMLElement childElement)) continue;
        // TODO: instanceof BodyElement
        if (!childElement.name().equals("body")) continue;
        PropertyContainer properties = ((ElementContext) childElement.getContext()).properties();
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
