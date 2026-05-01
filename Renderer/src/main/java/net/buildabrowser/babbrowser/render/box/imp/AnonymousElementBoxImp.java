package net.buildabrowser.babbrowser.render.box.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.flow.FlowRootContent;

public class AnonymousElementBoxImp extends AbstractElementBoxImp {

  private final ActiveStyles activeStyles;
  private final BoxContent content;

  public AnonymousElementBoxImp(ActiveStyles activeStyles, ElementBox parentBox, BoxLevel boxLevel) {
    super(parentBox, boxLevel);
    this.activeStyles = activeStyles;
    this.content = new FlowRootContent(this);
  }

  @Override
  public ActiveStyles activeStyles() {
    return this.activeStyles;
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
  
}
