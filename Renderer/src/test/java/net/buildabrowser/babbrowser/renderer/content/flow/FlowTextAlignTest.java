package net.buildabrowser.babbrowser.renderer.content.flow;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowInlineBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.FragmentTestUtil.assertFragmentEquals;
import static net.buildabrowser.babbrowser.renderer.content.flow.test.FlowLayoutUtil.doLayoutSized;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatValue;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.test.TestTextBox;
import net.buildabrowser.babbrowser.renderer.content.common.test.TestManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.flow.test.FlowLayoutUtil.FlowTestLayoutResult;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public class FlowTextAlignTest {

  @Test
  @DisplayName("Can align text to left")
  public void canAlignTextToLeft() {
    TestTextBox childBox = new TestTextBox("test");
    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.TEXT_ALIGN, TextAlignValue.LEFT);
    ElementBox parentBox = flowBlockBox(parentStyles, List.of(childBox));
    
    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);

    LayoutFragment expectedMainFragment = new TestManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new LineBoxFragment(0, 0, 20, 10, List.of(
        new TextFragment(0, 0, 20, 10, "test")))
    ));
    LayoutFragment actualMainFragment = layoutResult.rootFragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);
  }

  @Test
  @DisplayName("Can align text to center")
  public void canAlignTextToCenter() {
    TestTextBox childBox = new TestTextBox("test");
    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.TEXT_ALIGN, TextAlignValue.CENTER);
    ElementBox parentBox = flowBlockBox(parentStyles, List.of(childBox));
    
    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);

    LayoutFragment expectedMainFragment = new TestManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new LineBoxFragment(30, 0, 20, 10, List.of(
        new TextFragment(0, 0, 20, 10, "test")))
    ));
    LayoutFragment actualMainFragment = layoutResult.rootFragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);
  }

  @Test
  @DisplayName("Can align text to right")
  public void canAlignTextToRight() {
    TestTextBox childBox = new TestTextBox("test");
    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.TEXT_ALIGN, TextAlignValue.RIGHT);
    ElementBox parentBox = flowBlockBox(parentStyles, List.of(childBox));
    
    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);

    LayoutFragment expectedMainFragment = new TestManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new LineBoxFragment(60, 0, 20, 10, List.of(
        new TextFragment(0, 0, 20, 10, "test")))
    ));
    LayoutFragment actualMainFragment = layoutResult.rootFragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);
  }

  @Test
  @DisplayName("Can align text to center, with floats")
  public void canAlignTextToCenterWithFloats() {
    ActiveStyles leftFloatStyles = ActiveStyles.create();
    leftFloatStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    TestTextBox leftFloatTextBox = new TestTextBox("A");
    ElementBox leftFloatBox = flowInlineBox(leftFloatStyles, List.of(leftFloatTextBox));

    ActiveStyles rightFloatStyles = ActiveStyles.create();
    rightFloatStyles.setProperty(CSSProperty.FLOAT, FloatValue.RIGHT);
    TestTextBox rightFloatTextBox = new TestTextBox("CCC");
    ElementBox rightFloatBox = flowInlineBox(rightFloatStyles, List.of(rightFloatTextBox));

    TestTextBox childBox = new TestTextBox("test");
    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.TEXT_ALIGN, TextAlignValue.CENTER);
    ElementBox parentBox = flowBlockBox(parentStyles, List.of(leftFloatBox, rightFloatBox, childBox));
    
    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);

    LayoutFragment expectedMainFragment = new TestManagedBoxFragment(0, 0, 80, 10, parentBox, List.of(
      new LineBoxFragment(25, 0, 20, 10, List.of(
        new TextFragment(0, 0, 20, 10, "test")))
    ));
    LayoutFragment actualMainFragment = layoutResult.rootFragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);
  }
  
}
