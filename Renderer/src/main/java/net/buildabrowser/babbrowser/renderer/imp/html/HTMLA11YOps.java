package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class HTMLA11YOps implements A11YOps {

  private final SlotFamily<HTMLElement, ElementContext> elementContexts;

  public HTMLA11YOps(
    SlotFamily<HTMLElement, ElementContext> elementContexts
  ) {
    this.elementContexts = elementContexts;
  }

  @Override
  public boolean isSkipped(Node node) {
    if (!(
      node instanceof HTMLElement htmlElement
    )) return false;

    ElementContext context = elementContexts.get(htmlElement);
    return PropertiesUtil.outerDisplayValue(context.properties())
      .equals(OuterDisplayValue.NONE);
  }

  @Override
  public boolean isIgnored(Node node) {
    if (!(
      node instanceof HTMLElement htmlElement
    )) return false;

    ElementContext context = elementContexts.get(htmlElement);
    return PropertiesUtil.outerDisplayValue(context.properties())
      .equals(OuterDisplayValue.CONTENTS);
  }

  @Override
  public boolean hasSemanticMeaning(Element element) {
    if (!(
      element instanceof HTMLElement htmlElement
    )) return false;

    ElementContext context = elementContexts.get(htmlElement);
    return
      !context.properties().get(CSSProperty.OVERFLOW_X).equals(OverflowValue.VISIBLE)
      || !context.properties().get(CSSProperty.OVERFLOW_Y).equals(OverflowValue.VISIBLE);
  }
  
}
