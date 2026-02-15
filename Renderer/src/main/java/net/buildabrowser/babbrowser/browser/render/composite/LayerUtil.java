package net.buildabrowser.babbrowser.browser.render.composite;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;

public class LayerUtil {
  
  public static boolean startsLayer(ElementBox box) {
    return !box.activeStyles().getProperty(CSSProperty.POSITION).equals(PositionValue.STATIC);
  }

  public static boolean startsLayer(LayoutFragment fragment) {
    return
      fragment instanceof BoxFragment boxFragment
      && startsLayer(boxFragment.box());
  }

}
