package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class BoxGeneratorImp implements BoxGenerator {
  
  @Override
  public List<Box> box(Box parentBox, Node node) {
    return switch (node) {
      case Text text -> List.of(TextBox.create(text));
      case MutableElement element -> createElementBoxes(parentBox, element);
      default -> throw new UnsupportedOperationException("Unsupported Box Type");
    };
  }

  private List<Box> createElementBoxes(Box parentBox, MutableElement element) {
    ElementContext context = (ElementContext) element.getContext();
    OuterDisplayValue outerDisplayValue = context.activeStyles().outerDisplayValue();

    switch (outerDisplayValue) {
      case BLOCK:
        return createElementBox(parentBox, element, BoxLevel.BLOCK_LEVEL);
      case CONTENTS:
        return createChildBoxes(parentBox, element.childNodes());
      case INLINE:
        return createElementBox(parentBox, element, BoxLevel.INLINE_LEVEL);
      case NONE:
        return List.of();
      case RUN_IN:
        throw new UnsupportedOperationException("run-in boxes not supported!");
      default:
        throw new UnsupportedOperationException("Unrecognized box type!");
    }
  }

  private List<Box> createElementBox(Box parentBox, MutableElement element, BoxLevel boxLevel) {
    ElementBox elementBox = ElementBox.create(element, parentBox, boxLevel);
    for (Box childBox: createChildBoxes(elementBox, element.childNodes())) {
      elementBox.addChild(childBox);
    }

    return List.of(elementBox);
  }

  private List<Box> createChildBoxes(Box parentBox, NodeList children) {
    List<Box> childBoxes = new ArrayList<>((int) children.length());
    for (Node childNode: children) {
      childBoxes.addAll(box(parentBox, childNode));
    }

    return childBoxes;
  }

}
