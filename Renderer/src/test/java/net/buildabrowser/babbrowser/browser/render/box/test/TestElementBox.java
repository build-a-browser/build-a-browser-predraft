package net.buildabrowser.babbrowser.browser.render.box.test;

import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;

public class TestElementBox implements ElementBox {

  private final List<Box> childBoxes;
  private final ActiveStyles activeStyles;
  private final BoxContent content;
  private final ElementBoxDimensions dimensions;
  private final BoxLevel boxLevel;

  public TestElementBox(Function<ElementBox, BoxContent> contentFunc, BoxLevel boxLevel, ActiveStyles activeStyles, List<Box> childBoxes) {
    this.content = contentFunc.apply(this);
    this.activeStyles = activeStyles;
    this.childBoxes = childBoxes;
    this.boxLevel = boxLevel;
    this.dimensions = ElementBoxDimensions.create();
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

  @Override
  public ElementBoxDimensions dimensions() {
    return this.dimensions;
  }

  @Override
  public List<Box> childBoxes() {
    return this.childBoxes;
  }

  @Override
  public void addChild(Box box) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BoxLevel boxLevel() {
    return this.boxLevel;
  }
  
}
