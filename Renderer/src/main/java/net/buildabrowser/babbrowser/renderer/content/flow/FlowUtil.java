package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlowUtil {
  
  private FlowUtil() {}

  public static float constraintWidth(
    ElementBox box, LayoutConstraint layoutConstraint
  ) {
    return switch (layoutConstraint.type()) {
      case BOUNDED -> layoutConstraint.value();
      case MIN_CONTENT -> EBDimensionsUtil.preferredMinWidthConstraint(box);
      case MAX_CONTENT -> EBDimensionsUtil.preferredWidthConstraint(box);
      default -> throw new UnsupportedOperationException("Unsupported constraint type!");
    };
  }

  public static float constraintHeight(ElementBox box, LayoutConstraint layoutConstraint) {
    return switch (layoutConstraint.type()) {
      case BOUNDED -> layoutConstraint.value();
      case AUTO -> 0;
      // Return 0 for now, we're not tracking the heights yet anyway
      case MIN_CONTENT -> 0; // throw new UnsupportedOperationException("Not yet implemented!");
      case MAX_CONTENT -> 0; //throw new UnsupportedOperationException("Not yet implemented!");
      default -> throw new UnsupportedOperationException("Unsupported constraint type!");
    };
  }

  public static boolean isBlockLevel(Box childBox) {
    return switch(childBox) {
      case ElementBox elementBox -> elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL);
      case TextBox _1 -> false;
      default -> throw new UnsupportedOperationException("Unknown box type!");
    };
  }

  public static boolean isInFlow(ElementBox elementBox) {
    return
      PropertiesUtil.innerDisplayValue(elementBox.properties()).equals(InnerDisplayValue.FLOW)
      && !elementBox.isReplaced()
      && PositionUtil.affectsLayout(elementBox)
      && !CompositeLayerUtil.hasScrollContent(elementBox);
  }

  public static boolean isFloat(ElementBox elementBox) {
    return !elementBox.properties().get(CSSProperty.FLOAT).equals(CSSValue.NONE);
  }

}
