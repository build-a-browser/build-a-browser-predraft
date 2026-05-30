package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.BorderUtil;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class TableComputedBorders {

  public ComputedBorder topBorder;
  
  public ComputedBorder bottomBorder;

  public ComputedBorder leftBorder;

  public ComputedBorder rightBorder;

  public static record ComputedBorder(
    ElementBox sourceBox,
    BorderSide sourceSide,
    float borderWidth,
    CSSValue borderStyle
  ) {
    
    public int borderColor() {
      CSSProperty colorProperty = switch (sourceSide) {
        case LEFT -> CSSProperty.BORDER_LEFT_COLOR;
        case RIGHT -> CSSProperty.BORDER_RIGHT_COLOR;
        case TOP -> CSSProperty.BORDER_TOP_COLOR;
        case BOTTOM -> CSSProperty.BORDER_BOTTOM_COLOR;
      };

      return ElementBorderPainter.borderColor(
        sourceBox.activeStyles(), colorProperty);
    }

  }

  public static enum BorderSide {
    BOTTOM, RIGHT, TOP, LEFT;
  }

  // TODO: Include column/row borders
  public static ComputedBorder computeBorder(
    ElementBox sourceBox,
    BorderSide sourceSide,
    LayoutConstraint referenceConstraint,
    boolean divTwo // So we don't have to allocate another ComputedBorder later
  ) {
    CSSProperty widthProperty = switch (sourceSide) {
      case TOP -> CSSProperty.BORDER_TOP_WIDTH;
      case BOTTOM -> CSSProperty.BORDER_BOTTOM_WIDTH;
      case LEFT -> CSSProperty.BORDER_LEFT_WIDTH;
      case RIGHT -> CSSProperty.BORDER_RIGHT_WIDTH;
    };

    CSSProperty styleProperty = switch (sourceSide) {
      case TOP -> CSSProperty.BORDER_TOP_STYLE;
      case BOTTOM -> CSSProperty.BORDER_BOTTOM_STYLE;
      case LEFT -> CSSProperty.BORDER_LEFT_STYLE;
      case RIGHT -> CSSProperty.BORDER_RIGHT_STYLE;
    };

    ActiveStyles sourceStyles = sourceBox.activeStyles();
    CSSValue widthValue = sourceStyles.getProperty(widthProperty);
    CSSValue borderStyle = sourceStyles.getProperty(styleProperty);
    float borderWidth = BorderUtil.computeBorder(widthValue, borderStyle, sourceBox, referenceConstraint);
    if (divTwo) borderWidth /= 2;

    return new ComputedBorder(sourceBox, sourceSide, borderWidth, borderStyle);
  }

}