package net.buildabrowser.babbrowser.browser.render.content.flow.test;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.test.TestElementBox;
import net.buildabrowser.babbrowser.browser.render.box.test.TestFixedSizeReplacedContent;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FlowBoxTestUtil {
  
  private FlowBoxTestUtil() {}


  public static ElementBox sizedReplacedBlockBox(int width, int height) {
    ActiveStyles childrenStyles = ActiveStyles.create();
    ElementBox myBox = new TestElementBox(
      box -> new TestFixedSizeReplacedContent(box, width, height), BoxLevel.BLOCK_LEVEL, childrenStyles, List.of());
    myBox.dimensions().setIntrinsicWidth(width);
    myBox.dimensions().setInstrinsicHeight(height);
    return myBox;
  }

  public static ElementBox sizedReplacedInlineBlockBox(int width, int height) {
    return sizedReplacedInlineBlockBox(ActiveStyles.create(), width, height);
  }

  public static ElementBox sizedReplacedInlineBlockBox(ActiveStyles styles, int width, int height) {
    styles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(OuterDisplayValue.BLOCK, InnerDisplayValue.FLOW_ROOT));
    TestElementBox myBox = new TestElementBox(
      box -> new TestFixedSizeReplacedContent(box, width, height), BoxLevel.INLINE_LEVEL, styles, List.of());
    myBox.dimensions().setIntrinsicWidth(width);
    myBox.dimensions().setInstrinsicHeight(height);
    return myBox;
  }


  public static ElementBox flowBlockBox(List<Box> children) {
    return flowBlockBox(ActiveStyles.create(), children);
  }

  public static ElementBox flowBlockBox(ActiveStyles styles, List<Box> children) {
    ElementBox parentBox = new TestElementBox(
      box -> new FlowRootContent(box),
      BoxLevel.BLOCK_LEVEL, styles, children);

    return parentBox;
  }

  public static ElementBox flowInlineBox(List<Box> children) {
    return flowInlineBox(ActiveStyles.create(), children);
  }

  public static ElementBox flowInlineBox(ActiveStyles styles, List<Box> children) {
    ElementBox parentBox = new TestElementBox(
      box -> new FlowRootContent(box),
      BoxLevel.INLINE_LEVEL, styles, children);

    return parentBox;
  }

  public static ElementBox flowInlineBlockBox(List<Box> children) {
    return flowInlineBlockBox(ActiveStyles.create(), children);
  }

  public static ElementBox flowInlineBlockBox(ActiveStyles styles, List<Box> children) {
    styles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(OuterDisplayValue.BLOCK, InnerDisplayValue.FLOW_ROOT));
    ElementBox parentBox = new TestElementBox(
      box -> new FlowRootContent(box),
      BoxLevel.INLINE_LEVEL, styles, children);

    return parentBox;
  }

}
