package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignValue;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.hintattr.AlignAttributeResolver;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlowAlignUtil {
  
  private FlowAlignUtil() {}

  public static float alignFragment(
    PropertyContainer lineProperties,
    float startPos, float endPos, float lineWidth
  ) {
    TextAlignValue textAlign = (TextAlignValue) lineProperties.get(CSSProperty.TEXT_ALIGN);
    while (
      textAlign.equals(TextAlignValue.MATCH_PARENT)
      && lineProperties.parent() != null
    ) {
      lineProperties = lineProperties.parent();
      textAlign = (TextAlignValue) lineProperties.get(CSSProperty.TEXT_ALIGN);
    }


    return switch (textAlign) {
      // TODO: Once rtl is supported, obey rtl
      case START -> startPos;
      case END -> endPos - lineWidth;

      case LEFT, _BAB_LEFT -> startPos;
      case CENTER, _BAB_CENTER -> startPos + (endPos - startPos) / 2 - lineWidth / 2;
      case RIGHT, _BAB_RIGHT -> endPos - lineWidth;

      // TODO: Properly implement these
      case JUSTIFY, _BAB_JUSTIFY -> startPos;
      case JUSTIFY_ALL -> startPos;
      // MATCH_PARENT remains unresolved, default to START
      case MATCH_PARENT -> startPos;

      default -> throw new UnsupportedOperationException("Unrecognized value: " + textAlign);
    };
  }



  public static float legacyAlign(
    BlockFormattingContext parentContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint childWidthConstraint,
    float lineStart, float lineEnd
  ) {
    float[] margin = childBox.dimensions().getComputedMargin();
    if (
      parentWidthConstraint.isPreLayoutConstraint()
      || childWidthConstraint.isPreLayoutConstraint()
    ) {
      return margin[2];
    }

    TextAlignValue parentAlign = (TextAlignValue) parentContext.properties().get(CSSProperty.TEXT_ALIGN);

    Element childElement = childBox.element();
    CSSValue childAlign =
      childBox.element() == null ? null :
      AlignAttributeResolver.resolveAlignValue(
        childElement.name(),
        childElement.getAttribute("align"));
    if (childAlign != null) {
      return margin[2];
    }

    if (!(parentAlign.alignsDescendants())) {
      return margin[2];
    }

    PropertyContainer childProperties = childBox.properties();
    if (
      childProperties.get(CSSProperty.MARGIN_LEFT).equals(CSSValue.AUTO)
      || childProperties.get(CSSProperty.MARGIN_RIGHT).equals(CSSValue.AUTO)
    ) return margin[2];

    // TODO: Also need to check if it is overconstrained

    float alignStart =
      parentWidthConstraint.isPreLayoutConstraint() ? 0 :
      alignDescendantFragment(
        parentAlign,
        lineStart, lineEnd, childWidthConstraint.value());
    return alignStart;
  }

  private static float alignDescendantFragment(
    TextAlignValue textAlign,
    float startPos, float endPos, float lineWidth
  ) {
    return switch (textAlign) {
      case _BAB_LEFT, _BAB_JUSTIFY -> startPos;
      case _BAB_CENTER -> startPos + (endPos - startPos) / 2 - lineWidth / 2;
      case _BAB_RIGHT -> endPos - lineWidth;
      default -> throw new IllegalArgumentException("Can't use this method on non-legacy alignments!");
    };
  }

}
