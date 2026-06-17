package net.buildabrowser.babbrowser.renderer.content.flow;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowInlineBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.FragmentTestUtil.assertFragmentEquals;
import static net.buildabrowser.babbrowser.renderer.content.flow.test.FlowLayoutUtil.doLayout;
import static net.buildabrowser.babbrowser.renderer.content.flow.test.FlowLayoutUtil.doLayoutSized;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.box.test.TestTextBox;
import net.buildabrowser.babbrowser.renderer.content.common.test.TestFloatRefFragment;
import net.buildabrowser.babbrowser.renderer.content.common.test.TestManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

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

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(35, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, innerFragment.width(Measurement.BORDER));
    Assertions.assertEquals(35, innerFragment.height(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment.posX(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment.posY(Measurement.BORDER));

    Assertions.assertEquals(25, innerFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(25, innerFragment.height(Measurement.CONTENT));
    Assertions.assertEquals(15, innerFragment.posX(Measurement.CONTENT));
    Assertions.assertEquals(10, innerFragment.posY(Measurement.CONTENT));
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

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(90, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment1 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, innerFragment1.width(Measurement.BORDER));
    Assertions.assertEquals(45, innerFragment1.height(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment1.posX(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment1.posY(Measurement.BORDER));

    Assertions.assertEquals(25, innerFragment1.width(Measurement.CONTENT));
    Assertions.assertEquals(25, innerFragment1.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment1.posX(Measurement.CONTENT));
    Assertions.assertEquals(10, innerFragment1.posY(Measurement.CONTENT));

    LayoutFragment innerFragment2 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(1);
    Assertions.assertEquals(40, innerFragment2.width(Measurement.BORDER));
    Assertions.assertEquals(45, innerFragment2.height(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment2.posX(Measurement.BORDER));
    Assertions.assertEquals(45, innerFragment2.posY(Measurement.BORDER));

    Assertions.assertEquals(25, innerFragment2.width(Measurement.CONTENT));
    Assertions.assertEquals(25, innerFragment2.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment2.posX(Measurement.CONTENT));
    Assertions.assertEquals(55, innerFragment2.posY(Measurement.CONTENT));
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

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(20, actualFragment.height(Measurement.CONTENT));

    LineBoxFragment lineBoxFragment = (LineBoxFragment) ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, lineBoxFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(20, lineBoxFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment = lineBoxFragment.fragments().get(0);
    Assertions.assertEquals(40, innerFragment.width(Measurement.BORDER));
    Assertions.assertEquals(20, innerFragment.height(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment.posX(Measurement.BORDER));
    Assertions.assertEquals(0, innerFragment.posY(Measurement.BORDER));

    Assertions.assertEquals(25, innerFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(10, innerFragment.height(Measurement.CONTENT));
    Assertions.assertEquals(15, innerFragment.posX(Measurement.CONTENT));
    Assertions.assertEquals(10, innerFragment.posY(Measurement.CONTENT));
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

    LayoutFragment expectedMainFragment = new TestManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new TestFloatRefFragment(childBox1),
      new LineBoxFragment(40, 0, 15, 10, List.of(
        new TextFragment(0, 0, 15, 10, "Off")))));
    LayoutFragment actualMainFragment = doLayoutSized(parentBox, 80).rootFragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);
  }

}