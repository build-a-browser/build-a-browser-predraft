package net.buildabrowser.babbrowser.renderer.box.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLText;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.BoxGenerator;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class BoxGeneratorImp implements BoxGenerator {
  
  @Override
  public List<Box> box(Box parentBox, Node node) {
    return switch (node) {
      case HTMLText text -> List.of(createTextBox(text));
      case HTMLElement element -> createElementBoxes(parentBox, element);
      case Comment _1 -> List.of();
      default -> throw new UnsupportedOperationException("Unsupported Box Type");
    };
  }


  @Override
  public void fixup(Box box) {
    if (box instanceof ElementBox elementBox) {
      BoxContent oldContent = elementBox.content();
      elementBox.update();

      boolean involvedFixup = !(
        elementBox.parentBox() instanceof ElementBox elParentBox
        && elementBox.content() == elParentBox.content());
      if (involvedFixup) {
        elementBox.content().fixupChildren();
      }

      boolean contentChanged = oldContent != elementBox.content();
      for (Box childBox: elementBox.childBoxes()) {
        boolean childInvalid =
          childBox instanceof ElementBox childElementBox
          && (
            childElementBox.element() == null
            || childElementBox.element().invalidationLevel().ordinal() <= InvalidationLevel.BOX.ordinal());
        if (involvedFixup || contentChanged || childInvalid) {
          fixup(childBox);
        }
      }
    }
  }

  private TextBox createTextBox(HTMLText text) {
    if (text.getBox() == null) {
      text.setBox(TextBox.create(text));
    }
    return (TextBox) text.getBox();
  }

  private List<Box> createElementBoxes(Box parentBox, HTMLElement element) {
    ElementContext context = (ElementContext) element.getContext();
    OuterDisplayValue outerDisplayValue = ActiveStylesUtil.outerDisplayValue(context.activeStyles());

    switch (outerDisplayValue) {
      case BLOCK:
        return createElementBox(parentBox, element, BoxLevel.BLOCK_LEVEL);
      case CONTENTS:
        element.setBox(null);
        return createChildBoxes(parentBox, element.childNodes());
      case INLINE:
        return createElementBox(parentBox, element, BoxLevel.INLINE_LEVEL);
      case NONE:
        clearBoxes(element);
        return List.of();
      case RUN_IN:
        throw new UnsupportedOperationException("run-in boxes not supported!");
      default:
        return createElementBox(parentBox, element, BoxLevel.INLINE_LEVEL);
    }
  }

  private void clearBoxes(Node node) {
    if (node instanceof HTMLElement element) {
      element.setBox(null);
      for (Node child: element.childNodes()) {
        clearBoxes(child);
      }
    } else if (node instanceof HTMLText text) {
      text.setBox(null);
    }
  }

  private List<Box> createElementBox(Box parentBox, HTMLElement element, BoxLevel boxLevel) {
    Box adjustedParentBox = parentBox;
    ElementBox scrollBox = null;
    if (CompositeLayerUtil.hasScrollContent(element)) {
      if (
        element.getBox() instanceof ElementBox elementBox
        && elementBox.parentBox() instanceof ScrollBox existingScrollBox
        && existingScrollBox.parentBox() == parentBox
      ) {
        adjustedParentBox = scrollBox = existingScrollBox;
      } else {
        adjustedParentBox = scrollBox = new ScrollBox(element, parentBox, boxLevel);
      }
    }

    boolean changedParent =
      element.getBox() == null
      || ((ElementBox) element.getBox()).parentBox() != adjustedParentBox;
    ElementBox elementBox;
    if (
      element.getBox() instanceof ElementBox elementBox2
      && element.invalidationLevel().ordinal() > InvalidationLevel.BOX.ordinal()
      && !changedParent
    ) {
      return List.of(scrollBox == null ? elementBox2 : scrollBox);
    } else if (element.getBox() instanceof ElementBox elementBox2) {
      // TODO: If we disable the fast path, there seems to be a memory leak here
      elementBox = elementBox2;
      elementBox.clearChildren();
      elementBox.updateDetails(adjustedParentBox, boxLevel);
      
      // TODO: It's not great to call invalidation while updating, but fixup needs to know if the parent
      // changed despite the box not being invalid. Validation occurs after painting, so the flag will not persist
      element.invalidate(InvalidationLevel.BOX);
    } else {
      elementBox = ElementBox.create(element, adjustedParentBox, boxLevel);
      element.setBox(elementBox);
      element.invalidate(InvalidationLevel.BOX);
    }
    for (Box childBox: createChildBoxes(elementBox, element.childNodes())) {
      elementBox.addChild(childBox);
    }

    if (scrollBox != null) {
      scrollBox.clearChildren();
      scrollBox.addChild(elementBox);
    }
    return List.of(scrollBox == null ? elementBox : scrollBox);
  }

  private List<Box> createChildBoxes(Box parentBox, NodeList children) {
    List<Box> childBoxes = new ArrayList<>((int) children.length());
    for (Node childNode: children) {
      childBoxes.addAll(box(parentBox, childNode));
    }

    return childBoxes;
  }

}
