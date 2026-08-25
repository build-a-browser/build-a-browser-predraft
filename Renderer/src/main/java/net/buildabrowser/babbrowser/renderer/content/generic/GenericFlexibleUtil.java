package net.buildabrowser.babbrowser.renderer.content.generic;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.isBlank;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public final class GenericFlexibleUtil {
  
  private GenericFlexibleUtil() {}

  public static void fixupChildren(ElementBox rootBox) {
    ElementBoxIterator childIt = rootBox.childBoxes();
    ElementBox anonymousBox = null;
    boolean isOnlyWhitespace = true;
    boolean didInsertBox = false;
    while (childIt.hasNext()) {
      switch (childIt.next()) {
        case ElementBox _1 -> {
          anonymousBox = null;
          didInsertBox = false;
        }
        case TextBox textBox -> {
          if (anonymousBox == null) {
            isOnlyWhitespace = true;
            // It's actually flex-level, but this flag has no effect regardless
            anonymousBox = ElementBox.createAnonymous(rootBox, BoxLevel.BLOCK_LEVEL);
          }
          isOnlyWhitespace = isOnlyWhitespace && isBlank(textBox.text()); // TODO: Proper HTML whitespace
          childIt.remove();
          anonymousBox.addChild(textBox);
        }
        default -> throw new UnsupportedOperationException("Don't know how to handle this type of box!");
      }

      if (
        anonymousBox != null
        && !isOnlyWhitespace
        && !didInsertBox
      ) {
        childIt.add(anonymousBox);
        didInsertBox = true;
      }
    }

    rootBox.sortChildren((a, b) -> Integer.compare(orderOf(a), orderOf(b)));
  }

  public static void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    UnmanagedBoxFragment<?> rootFragment = (UnmanagedBoxFragment<?>) fragment;
    ElementBox rootBox = rootFragment.box();
    rootFragment.setLayerPos(layerX, layerY);

    StackingContext refContext = rootBox.stackingContext();

    float offsetX = layerX + (rootFragment.posX(Measurement.CONTENT) - rootFragment.posX(Measurement.BORDER));
    float offsetY = layerY + (rootFragment.posY(Measurement.CONTENT) - rootFragment.posY(Measurement.BORDER));

    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      // Once again, fixup should have made everything ElementBox
      ElementBox childBox = (ElementBox) childIt.next();
      if (!PositionUtil.affectsLayout(childBox)) {
        childBox.alterDimensions(false, d -> d.setStaticPosition(layerX, layerY));
        continue;
      }
    }
  
    UnmanagedBoxFragment<?> childFragment = (UnmanagedBoxFragment<?>) rootFragment.innerFragment();
    while (childFragment != null) {
      ElementBox childBox = childFragment.box();
      float childX = offsetX + childFragment.posX(Measurement.BORDER);
      float childY = offsetY + childFragment.posY(Measurement.BORDER);
      
      if (childBox.stackingContext() != refContext) {
        childBox.stackingContext().positionFragment(
          childX, childY, childFragment,
          childBox.content()::positionLayers);
      } else {
        childFragment.setLayerPos(childX, childY);
        childBox.content().positionLayers(childFragment, childX, childY);
      }

      childFragment = (UnmanagedBoxFragment<?>) childFragment.next();
    }
  }

  public static UnmanagedBoxFragment<?> collectChildFragments(List<GenericItem> items) {
    UnmanagedBoxFragment<?> fragments = null;
    UnmanagedBoxFragment<?> lastFragment = null;
    for (GenericItem item: items) {
      // After fixup, grid should only have element children
      if (!PositionUtil.affectsLayout(item.box())) continue;
      UnmanagedBoxFragment<?> boxFragment = item.fragment();
      boxFragment.setNext(null);
      if (lastFragment == null) {
        fragments = lastFragment = boxFragment;
      } else {
        lastFragment = (UnmanagedBoxFragment<?>) IntrusiveList.add(lastFragment, boxFragment);
        lastFragment = (UnmanagedBoxFragment<?>) lastFragment.next();
      }
    }

    return fragments;
  }

  public static float mainGap(
    ElementBox rootBox,
    boolean isVertical, LayoutConstraint mainSize
  ) {
    PropertyContainer parentProperties = rootBox.properties();
    CSSValue mainGapValue = isVertical ?
      parentProperties.get(CSSProperty.ROW_GAP) :
      parentProperties.get(CSSProperty.COLUMN_GAP);
    LayoutConstraint mainGapConstraint = SizingUtil.evaluateBaseSize(
      rootBox.layoutContext(), mainSize, mainGapValue);
    return mainGapConstraint.isBounded() ? mainGapConstraint.value() : 0;
  }
  
  public static float crossGap(
    ElementBox rootBox,
    boolean isVertical, LayoutConstraint mainSize
  ) {
    return mainGap(rootBox, !isVertical, mainSize);
  }

  private static int orderOf(Box box) {
    assert box instanceof ElementBox;
    return ((OrderValue) ((ElementBox) box).properties().get(CSSProperty.ORDER)).order();
  }

}
