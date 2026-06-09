package net.buildabrowser.babbrowser.renderer.content.flow;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.renderer.content.common.test.FragmentTestUtil.assertFragmentEquals;
import static net.buildabrowser.babbrowser.renderer.content.flow.test.FlowLayoutUtil.doLayoutSized;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.test.TestManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;

public class FlowMinMaxSizeTest {
  
  @Test
  @DisplayName("Can layout child box with min-width")
  public void canLayoutChildBoxWithMinWidth() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.MIN_WIDTH, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 0, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 100, 0, childBox, List.of())));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with max-width")
  public void canLayoutChildBoxWithMaxWidth() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.MAX_WIDTH, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 0, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 20, 0, childBox, List.of())));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with min-width and width")
  public void canLayoutChildBoxWithMinWidthAndWidth() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(4, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MIN_WIDTH, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 0, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 20, 0, childBox, List.of())));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with max-width and width")
  public void canLayoutChildBoxWithMaxWidthAndWidth() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(4, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MAX_WIDTH, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 0, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 4, 0, childBox, List.of())));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with min-height")
  public void canLayoutChildBoxWithMinHeight() {
    ActiveStyles nestedChildStyles = ActiveStyles.create();
    nestedChildStyles.setProperty(CSSProperty.HEIGHT, PercentageValue.create(25));
    ElementBox nestedChildBox = flowBlockBox(nestedChildStyles, List.of());

    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.MIN_HEIGHT, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));

    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 40, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 100, 20, childBox, List.of(
        new TestManagedBoxFragment(0, 0, 100, 0, nestedChildBox, List.of())))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100, 40).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with min-height and height")
  public void canLayoutChildBoxWithMinHeightAndHeight() {
    ActiveStyles nestedChildStyles = ActiveStyles.create();
    nestedChildStyles.setProperty(CSSProperty.HEIGHT, PercentageValue.create(25));
    ElementBox nestedChildBox = flowBlockBox(nestedChildStyles, List.of());

    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(4, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MIN_HEIGHT, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox));

    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 40, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 100, 20, childBox, List.of(
        new TestManagedBoxFragment(0, 0, 100, 5, nestedChildBox, List.of())))));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100, 40).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with max-height and lesser height")
  public void canLayoutChildBoxWithMaxHeightAndLesserHeight() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(4, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MAX_HEIGHT, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 40, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 100, 4, childBox, List.of())));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100, 40).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

  @Test
  @DisplayName("Can layout child box with max-height and greater height")
  public void canLayoutChildBoxWithMaxHeightAndGreaterHeight() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(24, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.MAX_HEIGHT, LengthValue.create(20, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment expectedFragment = new TestManagedBoxFragment(0, 0, 100, 40, parentBox, List.of(
      new TestManagedBoxFragment(0, 0, 100, 20, childBox, List.of())));
    LayoutFragment actualFragment = doLayoutSized(parentBox, 100, 40).rootFragment();
    assertFragmentEquals(expectedFragment, actualFragment);
  }

}
