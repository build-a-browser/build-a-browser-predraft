package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public class AnonymousElementBoxImp extends AbstractElementBoxImp {

  private final PropertyContainer properties;
  private final BoxContent content;

  public AnonymousElementBoxImp(
    PropertyContainer properties,
    Box parentBox,
    BoxLevel boxLevel
  ) {
    super(parentBox, boxLevel);
    this.properties = properties;

    InnerDisplayValue innerDisplay = PropertiesUtil
      .innerDisplayValue(properties());
    this.content = createSpecifiedContent(innerDisplay);
  }

  @Override
  public BoxContent content() {
    return this.content;
  }

  @Override
  public HTMLElement element() {
    return null;
  }

  @Override
  public RenderContext context() {
    // TODO: Hopefully this is fine
    return ((ElementBox) parentBox()).context();
  }

  @Override
  public boolean isReplaced() {
    return false;
  }

  @Override
  public void update() {}


  @Override
  public LayoutContext layoutContext() {
    if (super.layoutContext() != null) {
      return super.layoutContext();
    }

    if (parentBox() instanceof ElementBox parentBox) {
      return parentBox.layoutContext();
    }

    return null;
  }

  @Override
  public PropertyContainer properties() {
    return this.properties;
  }

  @Override
  public StackingContext stackingContext() {
    StackingContext ownContext = super.stackingContext();
    if (ownContext != null) {
      return ownContext;
    }

    if (parentBox() instanceof ElementBox parentElBox) {
      return parentElBox.stackingContext();
    }

    return null;
  }
  
}
