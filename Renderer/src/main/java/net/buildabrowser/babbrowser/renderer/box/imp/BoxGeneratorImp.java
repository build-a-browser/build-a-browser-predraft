package net.buildabrowser.babbrowser.renderer.box.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.content.ContentValue;
import net.buildabrowser.babbrowser.cssbase.property.content.ContentValue.StringContentValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLText;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.BoxGenerator;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public class BoxGeneratorImp implements BoxGenerator {

  private final SlotFamily<HTMLElement, RenderContext> renderContexts;


  public BoxGeneratorImp(
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    this.renderContexts = renderContexts;
  }
  
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
        && isSameContent(elementBox, elParentBox));
      if (involvedFixup) {
        elementBox.content().fixupChildren(elementBox);
      }

      boolean contentChanged = oldContent != elementBox.content();
      for (Box childBox: elementBox.childBoxes()) {
        boolean childInvalid =
          childBox instanceof ElementBox childElementBox
          && (
            childElementBox.element() == null
            || childElementBox.context().invalidationLevel().ordinal() <= InvalidationLevel.BOX.ordinal());
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
    RenderContext context = renderContexts.get(element);
    OuterDisplayValue outerDisplayValue = PropertiesUtil.outerDisplayValue(context.properties());

    switch (outerDisplayValue) {
      case BLOCK:
        return createElementBox(parentBox, element, BoxLevel.BLOCK_LEVEL);
      case CONTENTS:
        if (renderContexts.get(element) instanceof ElementContext elementContext) {
          elementContext.setBox(null);
        }
        return createChildBoxes(parentBox, element);
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
      if (renderContexts.get(element) instanceof ElementContext elementContext) {
        elementContext.setBox(null);
      }
      element.forEachChild(child -> {
        clearBoxes(child);
      });
    } else if (node instanceof HTMLText text) {
      text.setBox(null);
    }
  }

  private List<Box> createElementBox(Box parentBox, HTMLElement element, BoxLevel boxLevel) {
    RenderContext context = renderContexts.get(element);
    Box adjustedParentBox = parentBox;
    ElementBox scrollBox = null;
    if (CompositeLayerUtil.hasScrollContent(context)) {
      if (
        context.box() instanceof ElementBox elementBox
        && elementBox.parentBox() instanceof ScrollBox existingScrollBox
        && existingScrollBox.parentBox() == parentBox
      ) {
        adjustedParentBox = scrollBox = existingScrollBox;
      } else {
        adjustedParentBox = scrollBox = new ScrollBox(context, parentBox, boxLevel);
      }
    }

    boolean changedParent =
      context.box() == null
      || ((ElementBox) context.box()).parentBox() != adjustedParentBox;
    ElementBox elementBox;
    if (
      context.box() instanceof ElementBox elementBox2
      && context.invalidationLevel().ordinal() > InvalidationLevel.BOX.ordinal()
      && !changedParent
    ) {
      return List.of(scrollBox == null ? elementBox2 : scrollBox);
    } else if (context.box() instanceof ElementBox elementBox2) {
      // TODO: If we disable the fast path, there seems to be a memory leak here
      elementBox = elementBox2;
      elementBox.clearChildren();
      elementBox.updateDetails(adjustedParentBox, boxLevel);
      
      // TODO: It's not great to call invalidation while updating, but fixup needs to know if the parent
      // changed despite the box not being invalid. Validation occurs after painting, so the flag will not persist
      context.invalidate(InvalidationLevel.BOX);
    } else {
      elementBox = ElementBox.create(context, adjustedParentBox, boxLevel);
      if (renderContexts.get(element) instanceof ElementContext elementContext) {
        elementContext.setBox(elementBox);
      }
      context.invalidate(InvalidationLevel.BOX);
    }

    addPseudoBoxIfNeeded(elementBox, SelectorTarget.BEFORE);
    for (Box childBox: createChildBoxes(elementBox, element)) {
      elementBox.addChild(childBox);
    }
    addPseudoBoxIfNeeded(elementBox, SelectorTarget.AFTER);

    if (scrollBox != null) {
      scrollBox.clearChildren();
      scrollBox.addChild(elementBox);
    }
    return List.of(scrollBox == null ? elementBox : scrollBox);
  }

  private void addPseudoBoxIfNeeded(ElementBox elementBox, SelectorTarget target) {
    // TODO: Need to call invalidate?
    if (!(
      elementBox.element() instanceof HTMLElement htmlElement
    )) return;

    RenderContext context = renderContexts.get(htmlElement);

    PropertyContainer pseudoProperties = context.targetedProperties(target);
    if (pseudoProperties == null) return;

    OuterDisplayValue outerDisplayValue = PropertiesUtil.outerDisplayValue(pseudoProperties);
    BoxLevel boxLevel =
      outerDisplayValue.equals(OuterDisplayValue.INLINE) ? BoxLevel.INLINE_LEVEL :
      outerDisplayValue.equals(OuterDisplayValue.BLOCK) ? BoxLevel.BLOCK_LEVEL :
      null;
    if (boxLevel == null) return;

    CSSValue contentValue = pseudoProperties.get(CSSProperty.CONTENT);
    if (
      !(contentValue instanceof ContentValue content)
      || content.equals(ContentValue.NORMAL)
    ) return;

    // TODO: Add a slot to the target to store the box
    ElementBox box = ElementBox.createAnonymous(pseudoProperties, elementBox, boxLevel);
    elementBox.addChild(box);

    switch (content) {
      case StringContentValue stringContent ->
        box.addChild(TextBox.create(Text.create(stringContent.content())));
      default -> throw new UnsupportedOperationException(
        "Unrecognized content type: " + content);
    }
  }


  private List<Box> createChildBoxes(Box parentBox, Node parent) {
    int length = 0;
    Node currentNode = parent.firstChild();
    while (currentNode != null) {
      length++;
      currentNode = currentNode.nextSibling();
    }

    List<Box> childBoxes = new ArrayList<>(length);
    parent.forEachChild(childNode -> {
      childBoxes.addAll(box(parentBox, childNode));
    });

    return childBoxes;
  }

  // Moved here because it is now used solely for determining if fixup is needed
  // (content sharing is no longer needed since the main types are now singletons)
  private boolean isSameContent(
    ElementBox box, ElementBox parentBox
  ) {
    CSSValue positioning = box.properties().get(CSSProperty.POSITION);
    return 
      box.element() != null
      && positioning.equals(PositionValue.STATIC)
      && !CompositeLayerUtil.hasScrollContent(box)
      && !typeAlwaysRoot(box)
      && !(parentBox instanceof ScrollBox)
      && parentBox.sharesContent(box);
  }

  private boolean typeAlwaysRoot(ElementBox box) {
    return switch (box.element().name()) {
      case "img", "input" -> true;
      default -> false;
    };
  }

}
