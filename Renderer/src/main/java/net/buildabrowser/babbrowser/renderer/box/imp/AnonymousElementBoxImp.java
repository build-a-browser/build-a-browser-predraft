package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public class AnonymousElementBoxImp extends AbstractElementBoxImp {

  private final PropertyContainer properties;
  private final BoxContent content;

  public AnonymousElementBoxImp(PropertyContainer properties, ElementBox parentBox, BoxLevel boxLevel) {
    super(parentBox, boxLevel);
    this.properties = properties;
    this.content = new FlowRootContent(this);
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
  
}
