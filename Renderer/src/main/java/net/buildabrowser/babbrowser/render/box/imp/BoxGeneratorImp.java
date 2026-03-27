package net.buildabrowser.babbrowser.render.box.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.render.box.TextBox;
import net.buildabrowser.babbrowser.render.context.ElementContext;

public class BoxGeneratorImp implements BoxGenerator {
  
  @Override
  public List<Box> box(Box parentBox, Node node) {
    return switch (node) {
      case Text text -> List.of(createTextBox(text));
      case Element element -> createElementBoxes(parentBox, element);
      case Comment _ -> List.of();
      default -> throw new UnsupportedOperationException("Unsupported Box Type");
    };
  }

  private TextBox createTextBox(Text text) {
    if (text.getBox() == null) {
      text.setBox(TextBox.create(text));
    }
    return (TextBox) text.getBox();
  }

  private List<Box> createElementBoxes(Box parentBox, Element element) {
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

  private List<Box> createElementBox(Box parentBox, Element element, BoxLevel boxLevel) {
    ElementBox elementBox;
    if (
      element.getBox() != null
      && ((ElementBox) element.getBox()).boxLevel().equals(boxLevel)
    ) {
      elementBox = (ElementBox) element.getBox();
      elementBox.clearChildren();
      elementBox.update();
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
