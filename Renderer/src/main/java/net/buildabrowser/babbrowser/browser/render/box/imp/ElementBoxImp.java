package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.content.ImageContent;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.browser.render.content.table.TableContent;
import net.buildabrowser.babbrowser.browser.render.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class ElementBoxImp extends AbstractElementBoxImp {

  private final MutableElement element;
  private final BoxContent content;

  public ElementBoxImp(MutableElement element, Box parentBox, BoxLevel boxLevel) {
    super(parentBox, boxLevel);
    this.element = element;
    this.content = createContent();
  }

  @Override
  public ActiveStyles activeStyles() {
    return ((ElementContext) element.getContext()).activeStyles();
  }

  @Override
  public BoxContent content() {
    return this.content;
  }

  @Override
  public Element element() {
    return this.element;
  }

  private BoxContent createContent() {
    if (element.name().equals("img")) {
      return new ImageContent(this);
    }
    
    InnerDisplayValue innerDisplay = activeStyles().innerDisplayValue();
    return switch (innerDisplay) {
      case TABLE -> new TableContent(this);
      default -> new FlowRootContent(this);
    };
  }
  
}
