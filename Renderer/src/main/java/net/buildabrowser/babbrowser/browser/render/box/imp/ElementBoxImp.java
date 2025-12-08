package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.box.content.ImageContent;
import net.buildabrowser.babbrowser.browser.render.box.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.browser.render.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class ElementBoxImp implements ElementBox {

  private final List<Box> childBoxes = new LinkedList<>();

  private final MutableElement element;
  private final BoxContent content;
  private final ElementBoxDimensions dimensions;
  private final Box parentBox;
  private final BoxLevel boxLevel;

  public ElementBoxImp(MutableElement element, Box parentBox, BoxLevel boxLevel) {
    this.element = element;
    this.content = element.name().equals("img") ?
      new ImageContent(this) :
      new FlowRootContent(this);
    this.dimensions = ElementBoxDimensions.create();
    this.parentBox = parentBox;
    this.boxLevel = boxLevel;
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

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    parentBox.invalidate(invalidationLevel);
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
    this.childBoxes.add(box);
  }

  @Override
  public BoxLevel boxLevel() {
    return this.boxLevel;
  }

  @Override
  public boolean isReplaced() {
    return element.name().equals("img");
  }
  
}
