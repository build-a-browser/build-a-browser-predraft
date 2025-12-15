package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.PercentageValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FlowWidthUtil {
  
  private FlowWidthUtil() {}

  public static LayoutConstraint evaluateBaseSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue,
    ActiveStyles childStyles
  ) {
    if (sizeValue instanceof LengthValue lengthValue) {
      return evaluateLengthBaseSize(layoutContext, lengthValue, childStyles);
    } else if (
      sizeValue instanceof PercentageValue percentageValue
      && parentConstraint.type().equals(LayoutConstraintType.BOUNDED)
    ) {
      // TODO: Is this the right width to compare against?
      return LayoutConstraint.of(percentageValue.value() * parentConstraint.value() / 100);
    } else {
      return parentConstraint;
    }
  }

  private static LayoutConstraint evaluateLengthBaseSize(
    LayoutContext layoutContext,
    LengthValue lengthValue,
    ActiveStyles childStyles
  ) {
    double baseValue = lengthValue.value().doubleValue();
    double sizeResult = switch (lengthValue.dimension()) {
      // TODO: Use real values for EM, EX
      case EM -> layoutContext.fontMetrics().fontHeight();
      case EX -> layoutContext.fontMetrics().fontHeight() / 2;
      case IN -> baseValue * 96;
      case CM -> baseValue * 96 / 2.54;
      case MM -> baseValue * 96 / 2.54 / 100;
      case PT -> baseValue / 0.75;
      case PC -> baseValue * 9;
      case PX -> baseValue;
      default -> throw new UnsupportedOperationException("Unknown Unit!");
    };

    return LayoutConstraint.of((int) sizeResult);
  }

}
