package net.buildabrowser.babbrowser.render.composite;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.render.context.ElementContext;

public class CompositeLayerUtil {
  
  private CompositeLayerUtil() {}

  public static boolean hasScrollContent(HTMLElement element) {
    ActiveStyles activeStyles = ((ElementContext) element.getContext()).activeStyles();
    CSSValue overflowX = activeStyles.getProperty(CSSProperty.OVERFLOW_X);
    CSSValue overflowY = activeStyles.getProperty(CSSProperty.OVERFLOW_Y);

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
        ActiveStyles activeStyles = ((ElementContext) childElement.getContext()).activeStyles();
        if (
          activeStyles.getProperty(CSSProperty.DISPLAY).equals(DisplayValue.NONE)
        ) continue;

        guessedValue = activeStyles.getProperty(relatedProperty);
        break;
      }
    }
    if (guessedValue.equals(OverflowValue.VISIBLE)) {
      return OverflowValue.AUTO;
    }

    return guessedValue;
  }

}
