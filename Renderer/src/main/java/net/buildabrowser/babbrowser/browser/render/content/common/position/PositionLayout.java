package net.buildabrowser.babbrowser.browser.render.content.common.position;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    LayoutContext layoutContext,
    ElementBox box
  ) {
    PosRefBoxFragment refFragment = new PosRefBoxFragment(box, layoutContext);
    return refFragment;
  }

  public static UnmanagedBoxFragment actuallyLayoutAbsolute(
    LayoutContext layoutContext,
    ElementBox refBox,
    float refWidth, float refHeight,
    float[] insets
  ) {
    BoxContent content = refBox.content();
    ElementBoxDimensions dimensions = refBox.dimensions();

    float containingWidth = refWidth - insets[2] - insets[3];
    float containingHeight = refHeight - insets[0] - insets[1];

    LayoutConstraint baseWidth = SizingUtil.evaluateBaseSize(
      layoutContext, LayoutConstraint.of(containingWidth),
      refBox.activeStyles().getProperty(CSSProperty.WIDTH));
    LayoutConstraint baseHeight = SizingUtil.evaluateBaseSize(
      layoutContext, LayoutConstraint.of(containingHeight),
      refBox.activeStyles().getProperty(CSSProperty.HEIGHT));

    // TODO: Handle sizes other than fit-content
    // TODO: Also clamp to max width and min width
    float fitContentWidth = Math.clamp(
      containingWidth,
      dimensions.preferredMinWidthConstraint(layoutContext),
      dimensions.preferredWidthConstraint(layoutContext));
    float usedWidth = baseWidth.type().equals(LayoutConstraintType.AUTO) ?
      fitContentWidth :
      baseWidth.value();
    
    // TODO: Actually determine a height to use

    UnmanagedBoxFragment itemFragment = content.layout(layoutContext,
      LayoutConstraint.of(usedWidth),
      baseHeight);
    itemFragment.setPos(0, 0);

    // TODO: Compute margins

    return itemFragment;
  }

  public static float[] positionAbsolute(
    float[] insets,
    UnmanagedBoxFragment computedFragment,
    float refWidth, float refHeight
  ) {
    ActiveStyles styles = computedFragment.box().activeStyles();
    boolean topInsetIsAuto = styles.getProperty(CSSProperty.TOP).equals(CSSValue.AUTO);
    boolean bottomInsetIsAuto = styles.getProperty(CSSProperty.BOTTOM).equals(CSSValue.AUTO);
    boolean leftInsetIsAuto = styles.getProperty(CSSProperty.LEFT).equals(CSSValue.AUTO);
    boolean rightInsetIsAuto = styles.getProperty(CSSProperty.RIGHT).equals(CSSValue.AUTO);

    float leftPos = positionAbsoluteAxis(
      leftInsetIsAuto, rightInsetIsAuto, insets, 2,
      computedFragment.borderWidth(), refWidth);
    float topPos = positionAbsoluteAxis(
      topInsetIsAuto, bottomInsetIsAuto, insets, 0,
      computedFragment.borderHeight(), refHeight);

    return new float[] { leftPos, topPos };
  }

  private static float positionAbsoluteAxis(
    boolean topInsetIsAuto,
    boolean bottomInsetIsAuto,
    float[] insets,
    int conIndex,
    float itemSize,
    float axisSize
  ) {
    if (bottomInsetIsAuto) {
      // TODO: Account for writing mode
      return insets[conIndex];
    } else if (topInsetIsAuto) {
      return axisSize - insets[conIndex + 1] - itemSize;
    } else {
      // TODO: Compute based on margins (for now, we'll use stronger inset)
      return insets[conIndex];
    }
  }

}
