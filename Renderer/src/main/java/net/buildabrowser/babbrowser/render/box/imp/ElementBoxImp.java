package net.buildabrowser.babbrowser.render.box.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.content.ImageContent;
import net.buildabrowser.babbrowser.render.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.render.content.table.TableContent;
import net.buildabrowser.babbrowser.render.context.ElementContext;

public class ElementBoxImp extends AbstractElementBoxImp {

  private final HTMLElement element;
  
  private BoxContent content;
  // TODO: Avoid this extra field
  private InnerDisplayValue prevDisplayValue;

  public ElementBoxImp(HTMLElement element, Box parentBox, BoxLevel boxLevel) {
    super(parentBox, boxLevel);
    this.element = element;
    update();
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
  public HTMLElement element() {
    return this.element;
  }
  
  @Override
  public void update() {
    InnerDisplayValue innerDisplay = activeStyles().innerDisplayValue();
    if (!innerDisplay.equals(prevDisplayValue)) {
      this.prevDisplayValue = innerDisplay;
      this.content = createContent(innerDisplay);
    }
  }

  private BoxContent createContent(InnerDisplayValue innerDisplay) {
    if (element.name().equals("img")) {
      return new ImageContent(this);
    }
  
    return switch (innerDisplay) {
      case TABLE -> new TableContent(this);
      case FLEX -> new FlexBoxContent(this);
      default -> new FlowRootContent(this);
    };
  }
  
}
