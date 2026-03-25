package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.context.ElementContext;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.mutable.MutableText;

public class BoxGeneratorImp implements BoxGenerator {
  
  @Override
  public List<Box> box(Box parentBox, Node node) {
    return switch (node) {
      case MutableText text -> List.of(createTextBox(text));
      case MutableElement element -> createElementBoxes(parentBox, element);
      case Comment _ -> List.of();
      default -> throw new UnsupportedOperationException("Unsupported Box Type");
    };
  }

  private TextBox createTextBox(MutableText text) {
    if (text.getBox() == null) {
      text.setBox(TextBox.create(text));
    }
    return (TextBox) text.getBox();
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
        return createElementBox(parentBox, element, BoxLevel.INLINE_LEVEL);
    }
  }

  private List<Box> createElementBox(Box parentBox, MutableElement element, BoxLevel boxLevel) {
    ElementBox elementBox;
    if (element.getBox() != null) {
      elementBox = (ElementBox) element.getBox();
      elementBox.clearChildren();
    } else {
      elementBox = ElementBox.create(element, parentBox, boxLevel);
      element.setBox(elementBox);
    }
    for (Box childBox: createChildBoxes(elementBox, element.childNodes())) {
      elementBox.addChild(childBox);
    }
    elementBox.content().fixupChildren();

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
