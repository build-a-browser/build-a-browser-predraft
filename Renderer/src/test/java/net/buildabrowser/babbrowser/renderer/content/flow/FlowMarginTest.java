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
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
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

public class FlowMarginTest {
  
  @Test
  @DisplayName("Can layout sized block box with margin")
  public void canLayoutSizedBlockBoxWithMargin() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(24, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_LEFT, LengthValue.create(15, true, LengthType.PX));
    // Set a border to prevent the element itself from being ignored
    childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(35, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, innerFragment.width(Measurement.MARGIN));
    Assertions.assertEquals(35, innerFragment.height(Measurement.MARGIN));
    Assertions.assertEquals(0, innerFragment.posX(Measurement.MARGIN));
    Assertions.assertEquals(0, innerFragment.posY(Measurement.MARGIN));

    Assertions.assertEquals(25, innerFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment.height(Measurement.CONTENT));
    Assertions.assertEquals(15, innerFragment.posX(Measurement.CONTENT));
    Assertions.assertEquals(11, innerFragment.posY(Measurement.CONTENT));
  }

  @Test
  @DisplayName("Can layout two sized block boxes whose margins collapse")
  public void canLayoutTwoSizedBlockBoxesWhoseMarginsCollapse() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(24, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_BOTTOM, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_RIGHT, LengthValue.create(15, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);
    ElementBox childBox1 = flowBlockBox(childStyles, List.of());
    ElementBox childBox2 = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(80, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment1 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(25, innerFragment1.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment1.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment1.posX(Measurement.CONTENT));
    Assertions.assertEquals(11, innerFragment1.posY(Measurement.CONTENT));

    LayoutFragment innerFragment2 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(1);
    Assertions.assertEquals(25, innerFragment2.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment2.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment2.posX(Measurement.CONTENT));
    Assertions.assertEquals(46, innerFragment2.posY(Measurement.CONTENT));
  }

  @Test
  @DisplayName("Can layout inline box with text and margin")
  public void canLayoutInlineBoxWithTextAndMargin() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.MARGIN_LEFT, LengthValue.create(15, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW));
    TextBox nestedChildBox = new TestTextBox("HELLO");
    ElementBox childBox = flowInlineBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));

    LineBoxFragment lineBoxFragment = (LineBoxFragment) ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(40, lineBoxFragment.width(Measurement.CONTENT));

    LayoutFragment innerFragment = lineBoxFragment.fragments().get(0);
    Assertions.assertEquals(25, innerFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(10, innerFragment.height(Measurement.CONTENT));
    Assertions.assertEquals(15, innerFragment.posX(Measurement.CONTENT));
  }

  @Test
  @DisplayName("Can layout a left float with margin and offset other text")
  public void canLayoutALeftFloatWithMarginAndOffsetOtherText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    childStyles.setProperty(CSSProperty.MARGIN_LEFT, LengthValue.create(15, true, LengthType.PX));
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

  @Test
  @DisplayName("Can collapse margins through an empty box")
  public void canLayoutTwoSizedBlockBoxesWithMargin() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(24, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_BOTTOM, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_RIGHT, LengthValue.create(15, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);
    ElementBox childBox1 = flowBlockBox(childStyles, List.of());
    ElementBox childBox2 = flowBlockBox(ActiveStyles.create(), List.of());
    ElementBox childBox3 = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(80, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment1 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(25, innerFragment1.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment1.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment1.posX(Measurement.CONTENT));
    Assertions.assertEquals(11, innerFragment1.posY(Measurement.CONTENT));

    LayoutFragment innerFragment2 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(2);
    Assertions.assertEquals(25, innerFragment2.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment2.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment2.posX(Measurement.CONTENT));
    Assertions.assertEquals(46, innerFragment2.posY(Measurement.CONTENT));
  }

  @Test
  @DisplayName("Cannot collapse margins through an box with borders")
  public void cannotCollapseMarginsThroughABoxWithBorders() {
    ActiveStyles childStyles1 = ActiveStyles.create();
    childStyles1.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles1.setProperty(CSSProperty.HEIGHT, LengthValue.create(24, true, LengthType.PX));
    childStyles1.setProperty(CSSProperty.MARGIN_TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles1.setProperty(CSSProperty.MARGIN_BOTTOM, LengthValue.create(10, true, LengthType.PX));
    childStyles1.setProperty(CSSProperty.MARGIN_RIGHT, LengthValue.create(15, true, LengthType.PX));
    childStyles1.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
    childStyles1.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);

    ActiveStyles childStyles2 = ActiveStyles.create();
    childStyles2.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
    childStyles2.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);

    ElementBox childBox1 = flowBlockBox(childStyles1, List.of());
    ElementBox childBox2 = flowBlockBox(childStyles2, List.of());
    ElementBox childBox3 = flowBlockBox(childStyles1, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(91, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment1 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(25, innerFragment1.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment1.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment1.posX(Measurement.CONTENT));
    Assertions.assertEquals(11, innerFragment1.posY(Measurement.CONTENT));

    LayoutFragment innerFragment2 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(2);
    Assertions.assertEquals(25, innerFragment2.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment2.height(Measurement.CONTENT));
    Assertions.assertEquals(0, innerFragment2.posX(Measurement.CONTENT));
    Assertions.assertEquals(57, innerFragment2.posY(Measurement.CONTENT));
  }

  @Test
  @DisplayName("Can center box with auto margins")
  public void canCenterBoxWithAutoMargins() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(26, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(24, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MARGIN_LEFT, CSSValue.AUTO);
    childStyles.setProperty(CSSProperty.MARGIN_RIGHT, CSSValue.AUTO);
    childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);

    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment actualFragment = doLayoutSized(parentBox, 80).rootFragment();
    Assertions.assertEquals(80, actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(25, actualFragment.height(Measurement.CONTENT));

    LayoutFragment innerFragment = ((ManagedBoxFragment<?>) actualFragment).fragments().get(0);
    Assertions.assertEquals(26, innerFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(24, innerFragment.height(Measurement.CONTENT));
    Assertions.assertEquals(27, innerFragment.posX(Measurement.CONTENT));
    Assertions.assertEquals(1, innerFragment.posY(Measurement.CONTENT));
  }

  // TODO: Make these tests more detailed
  @Test
  @DisplayName("Can collapse bottom margin of last child with parent bottom")
  public void canCollapseBottomMarginOfLastChildWithParentBottom() {
      ActiveStyles parentStyles = ActiveStyles.create();
      parentStyles.setProperty(CSSProperty.MARGIN_BOTTOM, LengthValue.create(20, true, LengthType.PX));

      ActiveStyles childStyles = ActiveStyles.create();
      childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(50, true, LengthType.PX));
      childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(50, true, LengthType.PX));
      childStyles.setProperty(CSSProperty.MARGIN_BOTTOM, LengthValue.create(30, true, LengthType.PX));
      childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
      childStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);

      ElementBox childBox = flowBlockBox(childStyles, List.of());
      ElementBox parentBox = flowBlockBox(parentStyles, List.of(childBox));
      ElementBox rootBox = flowBlockBox(List.of(parentBox));

      LayoutFragment actualFragment = doLayout(rootBox);
      Assertions.assertEquals(81, actualFragment.height(Measurement.CONTENT)); 
  }

  @Test
  @DisplayName("Can collapse mixed positive and negative margins")
  public void canCollapseMixedMargins() {
      ActiveStyles childStyles1 = ActiveStyles.create();
      childStyles1.setProperty(CSSProperty.HEIGHT, LengthValue.create(50, true, LengthType.PX));
      childStyles1.setProperty(CSSProperty.MARGIN_BOTTOM, LengthValue.create(30, true, LengthType.PX));
      childStyles1.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
      childStyles1.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);

      ActiveStyles childStyles2 = ActiveStyles.create();
      childStyles2.setProperty(CSSProperty.HEIGHT, LengthValue.create(50, true, LengthType.PX));
      childStyles2.setProperty(CSSProperty.MARGIN_TOP, LengthValue.create(-10, true, LengthType.PX));
      childStyles2.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(1, true, LengthType.PX));
      childStyles2.setProperty(CSSProperty.BORDER_TOP_STYLE, BorderStyleValue.SOLID);

      ElementBox child1 = flowBlockBox(childStyles1, List.of());
      ElementBox child2 = flowBlockBox(childStyles2, List.of());
      ElementBox parentBox = flowBlockBox(List.of(child1, child2));

      LayoutFragment actualFragment = doLayout(parentBox);
      LayoutFragment fragment2 = ((ManagedBoxFragment<?>) actualFragment).fragments().get(1);

      Assertions.assertEquals(72, fragment2.posY(Measurement.CONTENT));
  }

}
