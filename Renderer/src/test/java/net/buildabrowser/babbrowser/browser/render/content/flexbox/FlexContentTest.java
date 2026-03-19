package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import static net.buildabrowser.babbrowser.browser.render.content.common.test.CommonBoxTestUtil.flexBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.common.test.CommonBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.common.test.FragmentTestUtil.assertFragmentListEquals;
import static net.buildabrowser.babbrowser.browser.render.content.flexbox.test.FlexLayoutUtil.doLayoutSized;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.test.TestTextBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;

public class FlexContentTest {

  /* Basically tests the same stuff as the flex-test.html that will be provided */

  @Test
  @DisplayName("Can layout traditional horizontal flexbox")
  public void canLayoutHorizontalFlexbox() {
    ElementBox child1 = flowBlockBox(List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(List.of(new TestTextBox("Blue")));
    ElementBox parentBox = flexBlockBox(List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 15, 10, child1),
      new UnmanagedBoxFragment(15, 0, 25, 10, child2),
      new UnmanagedBoxFragment(40, 0, 20, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 100).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout justify-content: space-around; flexbox")
  public void canLayoutSpaceAroundFlexbox() {
    ElementBox child1 = flowBlockBox(List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.JUSTIFY_CONTENT, JustifyContentValue.SPACE_AROUND);
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(5, 0, 15, 10, child1),
      new UnmanagedBoxFragment(30, 0, 25, 10, child2),
      new UnmanagedBoxFragment(65, 0, 20, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 90).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout flexbox with item width or basis")
  public void canLayoutFlexboxWithItemWidthOrBasis() {
    ActiveStyles child1Styles = ActiveStyles.create();
    child1Styles.setProperty(CSSProperty.WIDTH, LengthValue.create(4, true, LengthType.EM));
    ElementBox child1 = flowBlockBox(child1Styles, List.of(new TestTextBox("Red")));

    ActiveStyles child2Styles = ActiveStyles.create();
    child2Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(50));
    ElementBox child2 = flowBlockBox(child2Styles, List.of(new TestTextBox("Green")));

    ElementBox child3 = flowBlockBox(List.of(new TestTextBox("Blue")));
    ElementBox parentBox = flexBlockBox(List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 40, 10, child1),
      new UnmanagedBoxFragment(40, 0, 100, 10, child2),
      new UnmanagedBoxFragment(140, 0, 20, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 200).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout flexbox with total basis greater than width and no wrap")
  public void canLayoutFlexboxWithTotalBasisGreaterThanWidthAndNoWrap() {
    ActiveStyles child12Styles = ActiveStyles.create();
    child12Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));
    ActiveStyles child3Styles = ActiveStyles.create();
    child3Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(50));

    ElementBox child1 = flowBlockBox(child12Styles, List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(child12Styles, List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(child3Styles, List.of(new TestTextBox("Blue")));
    ElementBox parentBox = flexBlockBox(List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 20, 10, child1),
      new UnmanagedBoxFragment(20, 0, 20, 10, child2),
      new UnmanagedBoxFragment(40, 0, 25, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 65).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout flexbox with total basis greater than width and wrap")
  public void canLayoutFlexboxWithTotalBasisGreaterThanWidthAndWrap() {
    ActiveStyles child12Styles = ActiveStyles.create();
    child12Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));
    ActiveStyles child3Styles = ActiveStyles.create();
    child3Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(50));

    ElementBox child1 = flowBlockBox(child12Styles, List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(child12Styles, List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(child3Styles, List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.FLEX_WRAP, FlexWrapValue.WRAP);
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 26, 10, child1),
      new UnmanagedBoxFragment(26, 0, 26, 10, child2),
      new UnmanagedBoxFragment(0, 10, 32.5f, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 65).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout vertical flexbox")
  public void canLayoutVerticalFlexbox() {
    ActiveStyles child2Styles = ActiveStyles.create();
    child2Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(50));

    ElementBox child1 = flowBlockBox(List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(child2Styles, List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.FLEX_DIRECTION, FlexDirectionValue.COLUMN);
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 100, 10, child1),
      new UnmanagedBoxFragment(0, 10, 100, 25, child2),
      new UnmanagedBoxFragment(0, 35, 100, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 100, 50).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout vertical flexbox with grow")
  public void canLayoutVerticalFlexboxWithGrow() {
    ActiveStyles child13Styles = ActiveStyles.create();
    child13Styles.setProperty(CSSProperty.FLEX_GROW, FlexGrowValue.create(1));

    ActiveStyles child2Styles = ActiveStyles.create();
    child2Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));

    ElementBox child1 = flowBlockBox(child13Styles, List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(child2Styles, List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(child13Styles, List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.FLEX_DIRECTION, FlexDirectionValue.COLUMN);
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 100, 15, child1),
      new UnmanagedBoxFragment(0, 15, 100, 20, child2),
      new UnmanagedBoxFragment(0, 35, 100, 15, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 100, 50).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout reverse horizontal flexbox")
  public void canLayoutReverseHorizontalFlexbox() {
    ElementBox child1 = flowBlockBox(List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.FLEX_DIRECTION, FlexDirectionValue.ROW_REVERSE);
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(85, 0, 15, 10, child1),
      new UnmanagedBoxFragment(60, 0, 25, 10, child2),
      new UnmanagedBoxFragment(40, 0, 20, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 100).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout order-modified flexbox with wrap")
  public void canLayoutOrderModifiedFlexbox() {
    ActiveStyles child1Styles = ActiveStyles.create();
    child1Styles.setProperty(CSSProperty.ORDER, OrderValue.create(2));
    child1Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));
    ElementBox child1 = flowBlockBox(child1Styles, List.of(new TestTextBox("Red")));

    ActiveStyles child2Styles = ActiveStyles.create();
    child2Styles.setProperty(CSSProperty.ORDER, OrderValue.create(1));
    child2Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));
    ElementBox child2 = flowBlockBox(child2Styles, List.of(new TestTextBox("Green")));

    ActiveStyles child3Styles = ActiveStyles.create();
    child3Styles.setProperty(CSSProperty.ORDER, OrderValue.create(0));
    child3Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));
    ElementBox child3 = flowBlockBox(child3Styles, List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.FLEX_WRAP, FlexWrapValue.WRAP);
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 40, 10, child3),
      new UnmanagedBoxFragment(40, 0, 40, 10, child2),
      new UnmanagedBoxFragment(0, 10, 40, 10, child1)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 100).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  // TODO: Test for border/padding/margin
  
  @Test
  @DisplayName("Can layout flexbox with column-gap")
  public void canLayoutFlexboxWithColumnGap() {
    ElementBox child1 = flowBlockBox(List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.COLUMN_GAP, LengthValue.create(1, true, LengthType.EM));
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 15, 10, child1),
      new UnmanagedBoxFragment(25, 0, 25, 10, child2),
      new UnmanagedBoxFragment(60, 0, 20, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 100).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  @Test
  @DisplayName("Can layout flexbox with row gap")
  public void canLayoutFlexboxWithRowGap() {
    ActiveStyles child12Styles = ActiveStyles.create();
    child12Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));
    ActiveStyles child3Styles = ActiveStyles.create();
    child3Styles.setProperty(CSSProperty.FLEX_BASIS, PercentageValue.create(40));

    ElementBox child1 = flowBlockBox(child12Styles, List.of(new TestTextBox("Red")));
    ElementBox child2 = flowBlockBox(child12Styles, List.of(new TestTextBox("Green")));
    ElementBox child3 = flowBlockBox(child3Styles, List.of(new TestTextBox("Blue")));

    ActiveStyles parentStyles = ActiveStyles.create();
    parentStyles.setProperty(CSSProperty.FLEX_WRAP, FlexWrapValue.WRAP);
    parentStyles.setProperty(CSSProperty.ROW_GAP, LengthValue.create(1, true, LengthType.EM));
    ElementBox parentBox = flexBlockBox(parentStyles, List.of(child1, child2, child3));

    List<LayoutFragment> expectedFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 26, 10, child1),
      new UnmanagedBoxFragment(26, 0, 26, 10, child2),
      new UnmanagedBoxFragment(0, 20, 26, 10, child3)
    );

    LayoutFragment actualFragments = doLayoutSized(parentBox, 65).childFragments();
    assertFragmentListEquals(expectedFragments, actualFragments);
  }

  // TODO: Test for position
  
}