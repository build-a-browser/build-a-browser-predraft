package net.buildabrowser.babbrowser.renderer.content.flow;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.FragmentTestUtil.assertFragmentEquals;
import static net.buildabrowser.babbrowser.renderer.content.flow.test.FlowLayoutUtil.doLayoutSized;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.test.TestTextBox;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.TextFragment;

public class FlowMinMaxContentTest {

  @Test
  @DisplayName("Can layout block box with min-content-width block child with text")
  public void canLayoutBlockBoxWithMinContentWidthBlockChildWithText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, SizeValue.MIN_CONTENT);
    TestTextBox nestedChildBox = new TestTextBox("Hello World!");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    // Renderer does not yet strip the leading space (so width of the second line is 35)
    LayoutFragment expectedFragment = new ManagedBoxFragment(0, 0, 80, 20, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 35, 20, childBox, List.of(
        new LineBoxFragment(0, 0, 25, 10, List.of(
          new TextFragment(0, 0, 25, 10, "Hello"))),
        new LineBoxFragment(0, 10, 35, 10, List.of(
          new TextFragment(0, 0, 35, 10, " World!")))))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 80).fragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with max-content-width block child with text")
  public void canLayoutBlockBoxWithMaxContentWidthBlockChildWithText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, SizeValue.MAX_CONTENT);
    TestTextBox nestedChildBox = new TestTextBox("Hello World!");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new ManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 60, 10, childBox, List.of(
        new LineBoxFragment(0, 0, 60, 10, List.of(
          new TextFragment(0, 0, 60, 10, "Hello World!")))))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 80).fragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with fit-content-func-width block child with text at ideal size")
  public void canLayoutBlockBoxWithFitContentFuncWidthBlockChildWithTextAtIdealSize() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, SizeValue.FitContent.create(PercentageValue.create(50)));
    TestTextBox nestedChildBox = new TestTextBox("Hello World!");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    // Renderer does not yet strip the leading space (so width of the second line is 35)
    LayoutFragment expectedFragment = new ManagedBoxFragment(0, 0, 80, 20, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 40, 20, childBox, List.of(
        new LineBoxFragment(0, 0, 25, 10, List.of(
          new TextFragment(0, 0, 25, 10, "Hello"))),
        new LineBoxFragment(0, 10, 35, 10, List.of(
          new TextFragment(0, 0, 35, 10, " World!")))))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 80).fragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

    @Test
  @DisplayName("Can layout block box with fit-content-func-width block child with text at undersized")
  public void canLayoutBlockBoxWithFitContentFuncWidthBlockChildWithTextAtUndersized() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, SizeValue.FitContent.create(PercentageValue.create(50)));
    TestTextBox nestedChildBox = new TestTextBox("Hello World!");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    // Renderer does not yet strip the leading space (so width of the second line is 35)
    LayoutFragment expectedFragment = new ManagedBoxFragment(0, 0, 40, 20, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 35, 20, childBox, List.of(
        new LineBoxFragment(0, 0, 25, 10, List.of(
          new TextFragment(0, 0, 25, 10, "Hello"))),
        new LineBoxFragment(0, 10, 35, 10, List.of(
          new TextFragment(0, 0, 35, 10, " World!")))))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 40).fragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout block box with fit-content-func-width block child with text at oversized")
  public void canLayoutBlockBoxWithFitContentFuncWidthBlockChildWithTextAtOversized() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, SizeValue.FitContent.create(PercentageValue.create(50)));
    TestTextBox nestedChildBox = new TestTextBox("Hello World!");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new ManagedBoxFragment(0, 0, 400, 10, parentBox, List.of(
      new ManagedBoxFragment(0, 0, 60, 10, childBox, List.of(
        new LineBoxFragment(0, 0, 60, 10, List.of(
          new TextFragment(0, 0, 60, 10, "Hello World!")))))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 400).fragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }


}
