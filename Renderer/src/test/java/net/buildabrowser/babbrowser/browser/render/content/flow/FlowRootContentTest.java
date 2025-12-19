package net.buildabrowser.babbrowser.browser.render.content.flow;

import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowTestUtil.assertFragmentEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.test.TestElementBox;
import net.buildabrowser.babbrowser.browser.render.box.test.TestFixedSizeReplacedContent;
import net.buildabrowser.babbrowser.browser.render.box.test.TestTextBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.property.size.PercentageValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles.SizingUnit;

public class FlowRootContentTest {
  
  @Test
  @DisplayName("Can layout empty block box")
  public void canLayoutEmptyBlockBoxWithChild() {
    ElementBox parentBox = flowBlockBox(List.of());
    
    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, parentBox, List.of());
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with replaced child")
  public void canLayoutBlockBoxWithReplacedChild() {
    ElementBox childBox = sizedReplacedBlockBox(50, 50);
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 50, parentBox, List.of(
      new UnmanagedBoxFragment(0, 0, 50, 50, childBox)));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with two replaced children")
  public void canLayoutBlockBoxWithTwoReplacedChildren() {
    ElementBox childBox1 = sizedReplacedBlockBox(50, 50);
    ElementBox childBox2 = sizedReplacedBlockBox(50, 50);
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 100, parentBox, List.of(
      new UnmanagedBoxFragment(0, 0, 50, 50, childBox1),
      new UnmanagedBoxFragment(0, 50, 50, 50, childBox2)));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with one text child")
  public void canLayoutBlockBoxWithOneTextChild() {
    TestTextBox childBox = new TestTextBox("Hello");
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 25, 10, parentBox, List.of(
      new LineBoxFragment(0, 0, 25, 10, List.of(
        new TextFragment(0, 0, 25, 10, "Hello")))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with two text children")
  public void canLayoutBlockBoxWithTwoTextChildren() {
    TestTextBox childBox1 = new TestTextBox("Hello");
    TestTextBox childBox2 = new TestTextBox("World");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 10, parentBox, List.of(
      new LineBoxFragment(0, 0, 50, 10, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new TextFragment(25, 0, 25, 10, "World")))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with text child then block child")
  public void canLayoutBlockBoxWithTextChildThenBlockChild() {
    TestTextBox childBox1 = new TestTextBox("Hello");
    ElementBox childBox2 = sizedReplacedBlockBox(50, 50);
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 60, parentBox, List.of(
      new LineBoxFragment(0, 0, 25, 10, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"))),
      new UnmanagedBoxFragment(0, 10, 50, 50, childBox2)));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with block child then text child")
  public void canLayoutBlockBoxWithBlockChildThenTextChild() {
    ElementBox childBox1 = sizedReplacedBlockBox(50, 50);
    TestTextBox childBox2 = new TestTextBox("Hello");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 60, parentBox, List.of(
      new UnmanagedBoxFragment(0, 0, 50, 50, childBox1),
      new LineBoxFragment(0, 50, 25, 10, List.of(
        new TextFragment(0, 0, 25, 10, "Hello")))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with two block children then two text children")
  public void canLayoutBlockBoxWithTwoBlockChildrenThenTwoTextChildren() {
    ElementBox childBox1 = sizedReplacedBlockBox(50, 50);
    ElementBox childBox2 = sizedReplacedBlockBox(50, 50);
    TestTextBox childBox3 = new TestTextBox("Hello");
    TestTextBox childBox4 = new TestTextBox("World");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3, childBox4));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 110, parentBox, List.of(
      new UnmanagedBoxFragment(0, 0, 50, 50, childBox1),
      new UnmanagedBoxFragment(0, 50, 50, 50, childBox2),
      new LineBoxFragment(0, 100, 50, 10, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new TextFragment(25, 0, 25, 10, "World")))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with nested inline box with two text children")
  public void canLayoutBlockBoxWithNestedInlineBoxWithTwoTextChildren() {
    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    TestTextBox nestedChildBox2 = new TestTextBox("World");
    ElementBox nestingBox = flowInlineBox(List.of(nestedChildBox1, nestedChildBox2));
    ElementBox parentBox = flowBlockBox(List.of(nestingBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 10, parentBox, List.of(
      new LineBoxFragment(0, 0, 50, 10, List.of(
        new ManagedBoxFragment(0, 0, 50, 10, nestingBox, List.of(
          new TextFragment(0, 0, 25, 10, "Hello"),
          new TextFragment(25, 0, 25, 10, "World")))))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with text children and replaced inline-block box")
  public void canLayoutBlockBoxWithTextChildrenAndReplacedInlineBlockBox() {
    TestTextBox childBox1 = new TestTextBox("Hello");
    ElementBox childBox2 = sizedReplacedInlineBlockBox(10, 20);
    TestTextBox childBox3 = new TestTextBox("World");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    // TODO: This text might actually rely on the text's vertical alignment...
    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 60, 20, parentBox, List.of(
      new LineBoxFragment(0, 0, 60, 20, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new UnmanagedBoxFragment(25, 0, 10, 20, childBox2),
        new TextFragment(35, 0, 25, 10, "World")))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with text children and non-replaced inline-block box with nested replaced block-box children")
  public void canLayoutBlockBoxWithTextChildrenAndNonReplacedInlineBlockBoxWithNestedReplacedBlockBoxChildren() {
    ElementBox nestedChildBox1 = sizedReplacedBlockBox(20, 20);
    ElementBox nestedChildBox2 = sizedReplacedBlockBox(15, 30);
    TestTextBox childBox1 = new TestTextBox("Hello");
    ElementBox childBox2 = flowInlineBlockBox(List.of(nestedChildBox1, nestedChildBox2));
    TestTextBox childBox3 = new TestTextBox("World");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    // TODO: Find a way to test the inner box's layout
    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 70, 50, parentBox, List.of(
      new LineBoxFragment(0, 0, 70, 50, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new UnmanagedBoxFragment(25, 0, 20, 50, childBox2),
        new TextFragment(45, 0, 25, 10, "World")))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with complex nesting structure")
  public void canLayoutBlockBoxWithComplexNestingStructure() {
    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    TestTextBox nestedChildBox2 = new TestTextBox("World");
    ElementBox intermediateBox1 = flowInlineBox(List.of(nestedChildBox1, nestedChildBox2));
    ElementBox intermediateBox2 = flowBlockBox(List.of(nestedChildBox1));
    TestTextBox intermediateBox3 = new TestTextBox("!!!");
    ElementBox outerBox = flowInlineBox(List.of(intermediateBox1, intermediateBox2, intermediateBox3));
    ElementBox parentBox = flowBlockBox(List.of(outerBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(0, 0, 50, 30, parentBox, List.of(
      new LineBoxFragment(0, 0, 50, 10, List.of(
        new ManagedBoxFragment(0, 0, 50, 10, outerBox, List.of(
          new ManagedBoxFragment(0, 0, 50, 10, intermediateBox1, List.of(
            new TextFragment(0, 0, 25, 10, "Hello"),
            new TextFragment(25, 0, 25, 10, "World"))))))),
      // Importantly, this is reparented to parentBox
      new ManagedBoxFragment(0, 10, 25, 10, intermediateBox2, List.of(
        new LineBoxFragment(0, 0, 25, 10, List.of(
          new TextFragment(0, 0, 25, 10, "Hello"))))),
      new LineBoxFragment(0, 20, 15, 10, List.of(
      new ManagedBoxFragment(0, 0, 15, 10, outerBox, List.of(
        new TextFragment(0, 0, 15, 10, "!!!")))))));
    FlowFragment actualFragment = doLayout(parentBox);
    assertFragmentEquals(expectedFragment, actualFragment);
  }
  
  @Test
  @DisplayName("Can layout block box with absolute-width non-replaced block child")
  public void canLayoutBlockBoxWithAbsoluteWidthNonReplacedBlockChild() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setSizingProperty(SizingUnit.WIDTH, LengthValue.create(4, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(80, 0, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 4, 0, childBox, List.of())));
    FlowFragment actualFragment = doLayoutSized(parentBox, 80);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with percent-width non-replaced block child")
  public void canLayoutBlockBoxWithPercentWidthNonReplacedBlockChild() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setSizingProperty(SizingUnit.WIDTH, PercentageValue.create(25));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(80, 0, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 20, 0, childBox, List.of())));
    FlowFragment actualFragment = doLayoutSized(parentBox, 80);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with text and width-sized non-replaced inline-block child")
  public void canLayoutBlockBoxWithTextAndWidthSizedNonReplacedInlineBlockChild() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setSizingProperty(SizingUnit.WIDTH, PercentageValue.create(25));
    TestTextBox childBox1 = new TestTextBox("Hello");
    ElementBox childBox2 = flowInlineBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(80, 10, parentBox, List.of(
      new LineBoxFragment(0, 0, 45, 10, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new UnmanagedBoxFragment(25, 0, 20, 0, childBox2)))));
    FlowFragment actualFragment = doLayoutSized(parentBox, 80);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with text and ratio replaced inline-block child")
  public void canLayoutBlockBoxWithTextAndRatioReplacedInlineBlockChild() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setSizingProperty(SizingUnit.WIDTH, LengthValue.create(20, true, LengthType.PX));
    TestTextBox childBox1 = new TestTextBox("Hello");
    ElementBox childBox2 = sizedReplacedInlineBlockBox(childStyles, 40, 80);
    childBox2.dimensions().setIntrinsicRatio(.5f);
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(80, 40, parentBox, List.of(
      new LineBoxFragment(0, 0, 45, 40, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new UnmanagedBoxFragment(25, 0, 20, 40, childBox2)))));
    FlowFragment actualFragment = doLayoutSized(parentBox, 80);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with percent-height non-replaced block child")
  public void canLayoutBlockBoxWithPercentHeightNonReplacedBlockChild() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setSizingProperty(SizingUnit.HEIGHT, PercentageValue.create(25));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowFragment expectedFragment = new ManagedBoxFragment(80, 160, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 80, 40, childBox, List.of())));
    FlowFragment actualFragment = doLayoutSized(parentBox, 80, 160);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with text and sized non-replaced inline-block child")
  public void canLayoutBlockBoxWithTextAndSizedNonReplacedInlineBlockChild() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setSizingProperty(SizingUnit.WIDTH, LengthValue.create(5, true, LengthType.PX));
    childStyles.setSizingProperty(SizingUnit.HEIGHT, LengthValue.create(15, true, LengthType.PX));
    TestTextBox childBox1 = new TestTextBox("Hello");
    ElementBox childBox2 = flowInlineBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowFragment expectedFragment = new ManagedBoxFragment(80, 15, parentBox, List.of(
      new LineBoxFragment(0, 0, 30, 15, List.of(
        new TextFragment(0, 0, 25, 10, "Hello"),
        new UnmanagedBoxFragment(25, 0, 5, 15, childBox2)))));
    FlowFragment actualFragment = doLayoutSized(parentBox, 80);
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  private ElementBox sizedReplacedBlockBox(int width, int height) {
    ActiveStyles childrenStyles = ActiveStyles.create();
    ElementBox myBox = new TestElementBox(
      box -> new TestFixedSizeReplacedContent(box, width, height), BoxLevel.BLOCK_LEVEL, childrenStyles, List.of());
    myBox.dimensions().setIntrinsicWidth(width);
    myBox.dimensions().setInstrinsicHeight(height);
    return myBox;
  }

  private ElementBox sizedReplacedInlineBlockBox(int width, int height) {
    return sizedReplacedInlineBlockBox(ActiveStyles.create(), width, height);
  }

  private ElementBox sizedReplacedInlineBlockBox(ActiveStyles styles, int width, int height) {
    styles.setInnerDisplayValue(InnerDisplayValue.FLOW_ROOT);
    TestElementBox myBox = new TestElementBox(
      box -> new TestFixedSizeReplacedContent(box, width, height), BoxLevel.INLINE_LEVEL, styles, List.of());
    myBox.dimensions().setIntrinsicWidth(width);
    myBox.dimensions().setInstrinsicHeight(height);
    return myBox;
  }


  private ElementBox flowBlockBox(List<Box> children) {
    return flowBlockBox(ActiveStyles.create(), children);
  }

  private ElementBox flowBlockBox(ActiveStyles styles, List<Box> children) {
    ElementBox parentBox = new TestElementBox(
      box -> new FlowRootContent(box),
      BoxLevel.BLOCK_LEVEL, styles, children);

    return parentBox;
  }

  private ElementBox flowInlineBox(List<Box> children) {
    ActiveStyles styles = ActiveStyles.create();
    ElementBox parentBox = new TestElementBox(
      box -> new FlowRootContent(box),
      BoxLevel.INLINE_LEVEL, styles, children);

    return parentBox;
  }

  private ElementBox flowInlineBlockBox(List<Box> children) {
    return flowInlineBlockBox(ActiveStyles.create(), children);
  }

  private ElementBox flowInlineBlockBox(ActiveStyles styles, List<Box> children) {
    styles.setInnerDisplayValue(InnerDisplayValue.FLOW_ROOT);
    ElementBox parentBox = new TestElementBox(
      box -> new FlowRootContent(box),
      BoxLevel.INLINE_LEVEL, styles, children);

    return parentBox;
  }

  private FlowFragment doLayout(ElementBox parentBox) {
    LayoutContext layoutContext = new LayoutContext(TestFontMetrics.create(10, 5));
    FlowRootContent content = (FlowRootContent) parentBox.content();
    content.prelayout(layoutContext);
    content.layout(layoutContext, LayoutConstraint.AUTO, LayoutConstraint.AUTO);

    return content.rootFragment();
  }

  private FlowFragment doLayoutSized(ElementBox parentBox, int width) {
    LayoutContext layoutContext = new LayoutContext(TestFontMetrics.create(10, 5));
    FlowRootContent content = (FlowRootContent) parentBox.content();
    content.prelayout(layoutContext);
    content.layout(layoutContext, LayoutConstraint.of(width), LayoutConstraint.AUTO);

    return content.rootFragment();
  }

  private FlowFragment doLayoutSized(ElementBox parentBox, int width, int height) {
    LayoutContext layoutContext = new LayoutContext(TestFontMetrics.create(10, 5));
    FlowRootContent content = (FlowRootContent) parentBox.content();
    content.prelayout(layoutContext);
    content.layout(layoutContext, LayoutConstraint.of(width), LayoutConstraint.of(height));

    return content.rootFragment();
  }

}
