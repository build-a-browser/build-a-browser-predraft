package net.buildabrowser.babbrowser.browser.render.content.common.position;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    LayoutContext layoutContext,
    ElementBox box
  ) {
    PosRefBoxFragment refFragment = new PosRefBoxFragment(box, layoutContext);
    layoutContext.stackingContext().defer(refFragment);

    return refFragment;
  }

  public static UnmanagedBoxFragment actuallyLayoutAbsolute(
    PosRefBoxFragment refFragment,
    StackingContext stackingContext,
    float[] insets,
    CompositeLayer refLayer
  ) {
    LayoutContext refContext = refFragment.referenceLayoutContext();
    LayoutContext layoutContext = new LayoutContext(
      refContext.refURL(), refContext.fontMetrics(), stackingContext);
    ElementBox refBox = refFragment.referenceBox();
    BoxContent content = refBox.content();
    ElementBoxDimensions dimensions = refBox.dimensions();

    float containingWidth = refLayer.width() - insets[2] - insets[3];
    float containingHeight = refLayer.height() - insets[0] - insets[1];

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
      dimensions.preferredMinWidthConstraint(),
      dimensions.preferredWidthConstraint());
    float usedWidth = baseWidth.type().equals(LayoutConstraintType.AUTO) ?
      fitContentWidth :
      baseWidth.value();
    
    // TODO: Actually determine a height to use

    UnmanagedBoxFragment rootFragment = content.layout(layoutContext,
      LayoutConstraint.of(usedWidth),
      baseHeight);
    rootFragment.setPos(0, 0);

    // TODO: Compute margins

    return rootFragment;
  }

  public static float[] positionAbsolute(
    float[] insets,
    UnmanagedBoxFragment computedFragment,
    CompositeLayer refLayer
  ) {
    ActiveStyles styles = computedFragment.box().activeStyles();
    boolean topInsetIsAuto = styles.getProperty(CSSProperty.TOP).equals(CSSValue.AUTO);
    boolean bottomInsetIsAuto = styles.getProperty(CSSProperty.BOTTOM).equals(CSSValue.AUTO);
    boolean leftInsetIsAuto = styles.getProperty(CSSProperty.LEFT).equals(CSSValue.AUTO);
    boolean rightInsetIsAuto = styles.getProperty(CSSProperty.RIGHT).equals(CSSValue.AUTO);

    float leftPos = positionAbsoluteAxis(
      leftInsetIsAuto, rightInsetIsAuto, insets, 2,
      computedFragment.borderWidth(), refLayer.width());
    float topPos = positionAbsoluteAxis(
      topInsetIsAuto, bottomInsetIsAuto, insets, 0,
      computedFragment.borderHeight(), refLayer.height());

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
