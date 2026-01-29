package net.buildabrowser.babbrowser.browser.render.composite;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

public class LayerScannerUtil {
  
  public static boolean startsLayer(ElementBox box) {
    return !box.activeStyles().getProperty(CSSProperty.POSITION).equals(PositionValue.STATIC);
  }

  public static boolean startsLayer(LayoutFragment fragment) {
    return
      fragment instanceof BoxFragment boxFragment
      && startsLayer(boxFragment.box());
  }

  public static void scanLayers(CompositeLayer parent, LayoutFragment fragment) {
    scanLayersRecurse(parent, fragment, new int[] {
      -fragment.borderX(), // Will be increased by contentX in next call
      -fragment.borderY(),
    });
  }

  private static void createLayerForBox(
    CompositeLayer parentLayer,
    PosRefBoxFragment childBoxFragment,
    int[] offsets
  ) {
    CompositeLayer childLayer = parentLayer.createChild(childBoxFragment);
    positionBox(parentLayer, childLayer, childBoxFragment, offsets);
    if (actsReplaced(childBoxFragment)) {
      childBoxFragment.box().content().layer(childLayer);
    } else {
      scanLayers(childLayer, childBoxFragment);
    }
  }

  private static void positionBox(
    CompositeLayer parentLayer,
    CompositeLayer childLayer,
    PosRefBoxFragment childBoxFragment,
    int[] offsets
  ) {
    CSSValue position = childBoxFragment.box().activeStyles().getProperty(CSSProperty.POSITION);
    

    LayoutConstraint[] insets = PositionUtil.computeInsets(
      childBoxFragment.referenceLayoutContext(),
      childBoxFragment.box(),
      LayoutConstraint.of(parentLayer.width()),
      LayoutConstraint.of(parentLayer.height()));

    if (position.equals(PositionValue.RELATIVE)) {
      childLayer.incOffset(
        offsets[0] + childBoxFragment.borderX() + insets[2].value(),
        offsets[1] + childBoxFragment.borderY() + insets[0].value());
    } else if (position.equals(PositionValue.ABSOLUTE)) {
      childLayer.incOffset(
        insets[2].value(), insets[0].value());
    }
  }

  private static void scanLayersRecurse(
    CompositeLayer parentLayer,
    LayoutFragment fragment,
    int[] offsets
  ) {
    offsets[0] += fragment.contentX();
    offsets[1] += fragment.contentY();
    if (fragment instanceof ManagedBoxFragment parentFragment) {
      scanLayersManagedBoxFragment(parentLayer, parentFragment, offsets);
    } else if (fragment instanceof LineBoxFragment lineBoxFragment) {
      for (LayoutFragment childChildFragment: lineBoxFragment.fragments()) {
        scanLayersRecurse(parentLayer, childChildFragment, offsets);
      }
    }
    offsets[0] -= fragment.contentX();
    offsets[1] -= fragment.contentY();
  }

  private static void scanLayersManagedBoxFragment(
    CompositeLayer parentLayer,
    ManagedBoxFragment fragment,
    int[] offsets
  ) {
    for (LayoutFragment childFragment: fragment.fragments()) {
      if (
        childFragment instanceof PosRefBoxFragment childBoxFragment
        && startsLayer(childBoxFragment)
      ) {
        createLayerForBox(parentLayer, childBoxFragment, offsets);
      } else {
        scanLayersRecurse(parentLayer, childFragment, offsets);
      }
    }
  }

  private static boolean actsReplaced(BoxFragment fragment) {
    CSSValue position = fragment.box().activeStyles().getProperty(CSSProperty.POSITION);
    return
      !(position.equals(PositionValue.RELATIVE) || position.equals(PositionValue.STATIC))
      || (!position.equals(PositionValue.STATIC) && !position.equals(CSSValue.AUTO));
  }

}
