package net.buildabrowser.babbrowser.renderer.content.input.button;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.input.ButtonInputFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;

// TODO: I think buttons should also get their own stacking context
public class ButtonTypeContent implements InputTypeContent {
  
  private final String defaultValue;

  private BoxContent innerContent;

  public ButtonTypeContent(
    String defaultValue
  ) {
    this.defaultValue = defaultValue;
  }

  // TODO: Other button-like constraints
  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    String value = rootBox.element().getAttribute("value");
    if (value == null) value = defaultValue;
    // TODO: Cache the box?
    ActiveStyles anonStyles = ActiveStyles.create();
    anonStyles.setProperty(CSSProperty.DISPLAY, rootBox.properties().get(CSSProperty.DISPLAY));
    PropertyContainer anonProperties = ActiveStyles.parentStyles(rootBox.properties(), anonStyles);
    ElementBox innerBox = ElementBox.createAnonymous(anonProperties, rootBox, BoxLevel.BLOCK_LEVEL);
    innerBox.addChild(TextBox.create(Text.create(value)));
    this.innerContent = innerBox.content();
    UnmanagedBoxFragment<?> innerFragment = innerContent.layout(innerBox, widthConstraint, heightConstraint);
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();

    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, innerFragment.width(Measurement.CONTENT));
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, innerFragment.height(Measurement.CONTENT));

    // TODO: Need to properly use the line height
    if (
      usedHeight == 0
      && heightConstraint.type().equals(LayoutConstraintType.AUTO)
    ) {
      usedHeight = rootBox.layoutContext().font().metrics().height();
    }

    float inkWidth = Math.max(usedWidth, innerFragment.inkWidth(Measurement.CONTENT));
    float inkHeight = Math.max(usedHeight, innerFragment.inkHeight(Measurement.CONTENT));
    UnmanagedBoxFragment<?> buttonFragment = fragmentFactory.createButtonBoxFragment(
      usedWidth, usedHeight, inkWidth, inkHeight,
      // TODO: Properly compute baselines
      innerFragment.firstBaseline(Measurement.MARGIN),
      innerFragment.lastBaseline(Measurement.MARGIN),
      rootBox, innerFragment);
    buttonFragment.setPos(0, 0);
    return buttonFragment;
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    fragment.setLayerPos(layerX, layerY);
    float offsetX = layerX + (fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER));
    float offsetY = layerY + (fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER));

    // TODO: Why isn't innerContent already doing this?
    UnmanagedBoxFragment<?> innerFragment = ((ButtonInputFragment) fragment).innerFragment();
    innerFragment.setLayerPos(offsetX, offsetY);
    innerContent.positionLayers(innerFragment, offsetX, offsetY);
  }

}