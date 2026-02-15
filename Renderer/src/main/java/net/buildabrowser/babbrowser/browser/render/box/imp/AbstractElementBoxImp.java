package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.dom.Element;

public abstract class AbstractElementBoxImp implements ElementBox {
  
  private final List<Box> childBoxes = new LinkedList<>();

  private final ElementBoxDimensions dimensions;
  private final Box parentBox;
  private final BoxLevel boxLevel;

  public AbstractElementBoxImp(Box parentBox, BoxLevel boxLevel) {
    this.dimensions = ElementBoxDimensions.create();
    this.parentBox = parentBox;
    this.boxLevel = boxLevel;
  }

  @Override
  public Element element() {
    throw new UnsupportedOperationException("Anonymous box is not associated with an element!");
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
  public void removeChild(Box box) {
    this.childBoxes.remove(box);
  }

  @Override
  public void removeChild(int i) {
    this.childBoxes.remove(i);
  }

  @Override
  public void clearChildren() {
    this.childBoxes.clear();
  }

  @Override
  public BoxLevel boxLevel() {
    return this.boxLevel;
  }
  
}
