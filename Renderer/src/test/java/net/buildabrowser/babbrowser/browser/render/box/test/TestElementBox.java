package net.buildabrowser.babbrowser.browser.render.box.test;

import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.imp.AbstractElementBoxImp;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;

public class TestElementBox extends AbstractElementBoxImp {

  private final ActiveStyles activeStyles;
  private final BoxContent content;

  public TestElementBox(Function<ElementBox, BoxContent> contentFunc, BoxLevel boxLevel, ActiveStyles activeStyles, List<Box> childBoxes) {
    super(null, boxLevel);
    this.content = contentFunc.apply(this);
    this.activeStyles = activeStyles;
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
  public Element element() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    // No-op
  }
  
}
