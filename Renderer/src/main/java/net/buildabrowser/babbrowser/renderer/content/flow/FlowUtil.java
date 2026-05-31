package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlowUtil {
  
  private FlowUtil() {}

  public static float constraintWidth(
    ElementBoxDimensions dimensions, LayoutConstraint layoutConstraint
  ) {
    return switch (layoutConstraint.type()) {
      case BOUNDED -> layoutConstraint.value();
      case MIN_CONTENT -> dimensions.preferredMinWidthConstraint();
      case MAX_CONTENT -> dimensions.preferredWidthConstraint();
      default -> throw new UnsupportedOperationException("Unsupported constraint type!");
    };
  }

  public static float constraintHeight(ElementBoxDimensions dimensions, LayoutConstraint layoutConstraint) {
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
      ActiveStylesUtil.innerDisplayValue(elementBox.activeStyles()).equals(InnerDisplayValue.FLOW)
      && !elementBox.isReplaced()
      && PositionUtil.affectsLayout(elementBox)
      && !CompositeLayerUtil.hasScrollContent(elementBox);
  }

  public static boolean isFloat(ElementBox elementBox) {
    return !elementBox.activeStyles().getProperty(CSSProperty.FLOAT).equals(CSSValue.NONE);
  }

}
