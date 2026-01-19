package net.buildabrowser.babbrowser.browser.render.content.flow;

import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowInlineBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.doLayout;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.doLayoutContentSized;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowTestUtil.assertFragmentEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.box.test.TestTextBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class FlowPaddingTest {
  
  @Test
  @DisplayName("Can layout sized block box with padding")
  public void canLayoutSizedBlockBoxWithPadding() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_LEFT, LengthValue.create(15, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.contentWidth());
    Assertions.assertEquals(35, actualFragment.contentHeight());

    FlowFragment innerFragment = ((ManagedBoxFragment) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, innerFragment.borderWidth());
    Assertions.assertEquals(35, innerFragment.borderHeight());
    Assertions.assertEquals(0, innerFragment.borderX());
    Assertions.assertEquals(0, innerFragment.borderY());

    Assertions.assertEquals(25, innerFragment.contentWidth());
    Assertions.assertEquals(25, innerFragment.contentHeight());
    Assertions.assertEquals(15, innerFragment.contentX());
    Assertions.assertEquals(10, innerFragment.contentY());
  }

  @Test
  @DisplayName("Can layout two sized block boxes with padding")
  public void canLayoutTwoSizedBlockBoxesWithPadding() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_BOTTOM, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_RIGHT, LengthValue.create(15, true, LengthType.PX));
    ElementBox childBox1 = flowBlockBox(childStyles, List.of());
    ElementBox childBox2 = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.contentWidth());
    Assertions.assertEquals(90, actualFragment.contentHeight());

    FlowFragment innerFragment1 = ((ManagedBoxFragment) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, innerFragment1.borderWidth());
    Assertions.assertEquals(45, innerFragment1.borderHeight());
    Assertions.assertEquals(0, innerFragment1.borderX());
    Assertions.assertEquals(0, innerFragment1.borderY());

    Assertions.assertEquals(25, innerFragment1.contentWidth());
    Assertions.assertEquals(25, innerFragment1.contentHeight());
    Assertions.assertEquals(0, innerFragment1.contentX());
    Assertions.assertEquals(10, innerFragment1.contentY());

    FlowFragment innerFragment2 = ((ManagedBoxFragment) actualFragment).fragments().get(1);
    Assertions.assertEquals(40, innerFragment2.borderWidth());
    Assertions.assertEquals(45, innerFragment2.borderHeight());
    Assertions.assertEquals(0, innerFragment2.borderX());
    Assertions.assertEquals(45, innerFragment2.borderY());

    Assertions.assertEquals(25, innerFragment2.contentWidth());
    Assertions.assertEquals(25, innerFragment2.contentHeight());
    Assertions.assertEquals(0, innerFragment2.contentX());
    Assertions.assertEquals(55, innerFragment2.contentY());
  }

  @Test
  @DisplayName("Can layout inline box with text and padding")
  public void canLayoutInlineBoxWithTextAndPadding() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.PADDING_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_LEFT, LengthValue.create(15, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW));
    TextBox nestedChildBox = new TestTextBox("HELLO");
    ElementBox childBox = flowInlineBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.contentWidth());
    Assertions.assertEquals(20, actualFragment.contentHeight());

    LineBoxFragment lineBoxFragment = (LineBoxFragment) ((ManagedBoxFragment) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, lineBoxFragment.contentWidth());
    Assertions.assertEquals(20, lineBoxFragment.contentHeight());

    FlowFragment innerFragment = lineBoxFragment.fragments().get(0);
    Assertions.assertEquals(40, innerFragment.borderWidth());
    Assertions.assertEquals(20, innerFragment.borderHeight());
    Assertions.assertEquals(0, innerFragment.borderX());
    Assertions.assertEquals(0, innerFragment.borderY());

    Assertions.assertEquals(25, innerFragment.contentWidth());
    Assertions.assertEquals(10, innerFragment.contentHeight());
    Assertions.assertEquals(15, innerFragment.contentX());
    Assertions.assertEquals(10, innerFragment.contentY());
  }

  @Test
  @DisplayName("Can layout a left float with padding and offset other text")
  public void canLayoutALeftFloatWithPaddingAndOffsetOtherText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    childStyles.setProperty(CSSProperty.PADDING_LEFT, LengthValue.create(15, true, LengthType.PX));
    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    ElementBox childBox1 = flowInlineBox(childStyles, List.of(nestedChildBox1));
    TestTextBox childBox2 = new TestTextBox("Off");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowRootContent rootContent = doLayoutContentSized(parentBox, 80);

    FlowFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new LineBoxFragment(40, 0, 15, 10, List.of(
        new TextFragment(0, 0, 15, 10, "Off")))));
    FlowFragment actualMainFragment = rootContent.rootFragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);
  }

}