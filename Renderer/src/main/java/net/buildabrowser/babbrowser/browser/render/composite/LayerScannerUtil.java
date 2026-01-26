package net.buildabrowser.babbrowser.browser.render.composite;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.composite.imp.RootCompositeLayerImp;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
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

  public static void createLayerForBox(CompositeLayer parent, int[] offsets, BoxFragment childBoxFragment) {
    CompositeLayer childLayer = parent.createChild(childBoxFragment);
    CSSValue position = childBoxFragment.box().activeStyles().getProperty(CSSProperty.POSITION);
    if (position.equals(PositionValue.RELATIVE)) {
      childLayer.incOffset(
        offsets[0] + childBoxFragment.borderX(),
        offsets[1] + childBoxFragment.borderY());
    }
    if (actsReplaced(childBoxFragment)) {
      childBoxFragment.box().content().layer(childLayer);
    } else {
      scanLayers(childLayer, childBoxFragment);
    }
  }

  private static void scanLayersRecurse(CompositeLayer parent, LayoutFragment fragment, int[] offsets) {
    offsets[0] += fragment.contentX();
    offsets[1] += fragment.contentY();
    if (fragment instanceof ManagedBoxFragment parentFragment) {
      scanLayersManagedBoxFragment(parent, offsets, parentFragment);
    } else if (fragment instanceof LineBoxFragment lineBoxFragment) {
      for (LayoutFragment childChildFragment: lineBoxFragment.fragments()) {
        scanLayersRecurse(parent, childChildFragment, offsets);
      }
    }
    offsets[0] -= fragment.contentX();
    offsets[1] -= fragment.contentY();
  }

  private static void scanLayersManagedBoxFragment(CompositeLayer parent, int[] offsets, ManagedBoxFragment fragment) {
    if (parent instanceof RootCompositeLayerImp) {
      parent = parent.createChild(fragment);
    }

    for (LayoutFragment childFragment: fragment.fragments()) {
      if (
        childFragment instanceof BoxFragment childBoxFragment
        && startsLayer(childBoxFragment)
      ) {
        createLayerForBox(parent, offsets, childBoxFragment);
      } else {
        scanLayersRecurse(parent, childFragment, offsets);
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
