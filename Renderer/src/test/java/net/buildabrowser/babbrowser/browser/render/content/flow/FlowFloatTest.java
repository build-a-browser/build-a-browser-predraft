package net.buildabrowser.babbrowser.browser.render.content.flow;

import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowInlineBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.doLayoutSized;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowTestUtil.assertFragmentEquals;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowTestUtil.assertFragmentListEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.test.TestTextBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.FlowTestLayoutResult;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearValue;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class FlowFloatTest {

  // TODO: These tests currently have no visibility into the float itself, might be something to add...
  // also they're quite long

  @Test
  @DisplayName("Can layout a left float with inner text")
  public void canLayoutALeftFloatWithInnerText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);

    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox1));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 0, parentBox, List.of());
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(10, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 25, 10, childBox)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can layout a right float with inner text")
  public void canLayoutARightFloatWithInnerText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.RIGHT);

    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    ElementBox childBox = flowBlockBox(childStyles, List.of(nestedChildBox1));
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 0, parentBox, List.of());
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(10, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(55, 0, 25, 10, childBox)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  // Right now line start spaces are preserved (they should not be),
  // so this will need adjusted later...

  @Test
  @DisplayName("Can layout a left float and offset other text")
  public void canLayoutALeftFloatAndOffsetOtherText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    TestTextBox childBox1 = new TestTextBox("John is a human !!!");
    ElementBox childBox2 = flowInlineBox(childStyles, List.of(nestedChildBox1));
    TestTextBox childBox3 = new TestTextBox("Wow, text is long!");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 30, parentBox, List.of(
      new LineBoxFragment(0, 0, 75, 10, List.of(
        new TextFragment(0, 0, 75, 10, "John is a human"))),
      new LineBoxFragment(25, 10, 40, 10, List.of(
        new TextFragment(0, 0, 40, 10, " !!!Wow,"))),
      new LineBoxFragment(0, 20, 70, 10, List.of(
        new TextFragment(0, 0, 70, 10, " text is long!")))
    ));
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(30, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(0, 10, 25, 10, childBox2)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can layout a right float and offset other text")
  public void canLayoutARightFloatAndOffsetOtherText() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.RIGHT);
    TestTextBox nestedChildBox1 = new TestTextBox("Hello");
    TestTextBox childBox1 = new TestTextBox("John is a human !!!");
    ElementBox childBox2 = flowInlineBox(childStyles, List.of(nestedChildBox1));
    TestTextBox childBox3 = new TestTextBox("Wow, text is long!");
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 30, parentBox, List.of(
      new LineBoxFragment(0, 0, 75, 10, List.of(
        new TextFragment(0, 0, 75, 10, "John is a human"))),
      new LineBoxFragment(0, 10, 40, 10, List.of(
        new TextFragment(0, 0, 40, 10, " !!!Wow,"))),
      new LineBoxFragment(0, 20, 70, 10, List.of(
        new TextFragment(0, 0, 70, 10, " text is long!")))
    ));
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(30, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(55, 10, 25, 10, childBox2)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can explicitly size a float")
  public void canExplicitlySizeAFloat() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(5, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 0, parentBox, List.of());
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(15, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 5, 15, childBox)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can put multiple floats next to eachother")
  public void canPutMultipleFloatsNextToEachother() {
    ActiveStyles childStylesLeft = ActiveStyles.create();
    childStylesLeft.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    childStylesLeft.setProperty(CSSProperty.WIDTH, LengthValue.create(5, true, LengthType.PX));
    childStylesLeft.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ActiveStyles childStylesRight = ActiveStyles.create();
    childStylesRight.setProperty(CSSProperty.FLOAT, FloatValue.RIGHT);
    childStylesRight.setProperty(CSSProperty.WIDTH, LengthValue.create(5, true, LengthType.PX));
    childStylesRight.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ElementBox childBox1 = flowBlockBox(childStylesLeft, List.of());
    ElementBox childBox2 = flowBlockBox(childStylesLeft, List.of());
    ElementBox childBox3 = flowBlockBox(childStylesRight, List.of());
    ElementBox childBox4 = flowBlockBox(childStylesRight, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3, childBox4));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 0, parentBox, List.of());
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(15, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 5, 15, childBox1),
      new UnmanagedBoxFragment(5, 0, 5, 15, childBox2),
      new UnmanagedBoxFragment(75, 0, 5, 15, childBox3),
      new UnmanagedBoxFragment(70, 0, 5, 15, childBox4)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can drop float to next line on collision")
  public void canDropFloatToNextLineOnCollision() {
    ActiveStyles childStylesLeft = ActiveStyles.create();
    childStylesLeft.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    childStylesLeft.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStylesLeft.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ActiveStyles childStylesRight = ActiveStyles.create();
    childStylesRight.setProperty(CSSProperty.FLOAT, FloatValue.RIGHT);
    childStylesRight.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStylesRight.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ElementBox childBox1 = flowBlockBox(childStylesLeft, List.of());
    ElementBox childBox2 = flowBlockBox(childStylesLeft, List.of());
    ElementBox childBox3 = flowBlockBox(childStylesRight, List.of());
    ElementBox childBox4 = flowBlockBox(childStylesRight, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3, childBox4));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 0, parentBox, List.of());
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(30, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 25, 15, childBox1),
      new UnmanagedBoxFragment(25, 0, 25, 15, childBox2),
      new UnmanagedBoxFragment(55, 0, 25, 15, childBox3),
      new UnmanagedBoxFragment(55, 15, 25, 15, childBox4)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can intrude float into a block box")
  public void canIntrudeFloatIntoABlockBox() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.FLOAT, FloatValue.LEFT);
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(15, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    TestTextBox nestedChildBox = new TestTextBox("baba is you keke is flag");
    ElementBox childBox1 = flowInlineBox(childStyles, List.of());
    TestTextBox childBox2 = new TestTextBox("dragons");
    ElementBox childBox3 = flowBlockBox(List.of(nestedChildBox));
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2, childBox3));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 30, parentBox, List.of(
      new LineBoxFragment(15, 0, 35, 10, List.of(
        new TextFragment(0, 0, 35, 10, "dragons"))),
      new ManagedBoxFragment(0, 10, 80, 20, childBox3, List.of(
        new LineBoxFragment(15, 0, 55, 10, List.of(
          new TextFragment(0, 0, 55, 10, "baba is you")
        )),
        new LineBoxFragment(0, 10, 65, 10, List.of(
          new TextFragment(0, 0, 65, 10, " keke is flag")
        ))
      ))
    ));
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(30, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(0, 0, 15, 15, childBox1)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }

  @Test
  @DisplayName("Can clear a float")
  public void canClearAFloat() {
    ActiveStyles child1Styles = ActiveStyles.create();
    child1Styles.setProperty(CSSProperty.FLOAT, FloatValue.RIGHT);
    child1Styles.setProperty(CSSProperty.WIDTH, LengthValue.create(15, true, LengthType.PX));
    child1Styles.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ActiveStyles child2Styles = ActiveStyles.create();
    child2Styles.setProperty(CSSProperty.CLEAR, ClearValue.BOTH);
    child2Styles.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));

    ElementBox childBox1 = flowBlockBox(child1Styles, List.of());
    ElementBox childBox2 = flowBlockBox(child2Styles, List.of());
    child1Styles.setProperty(CSSProperty.HEIGHT, LengthValue.create(15, true, LengthType.PX));
    ElementBox parentBox = flowBlockBox(List.of(childBox1, childBox2));

    FlowTestLayoutResult layoutResult = doLayoutSized(parentBox, 80);;

    LayoutFragment expectedMainFragment = new ManagedBoxFragment(0, 0, 80, 30, parentBox, List.of(
      new ManagedBoxFragment(0, 15, 80, 15, childBox2, List.of())));
    LayoutFragment actualMainFragment = layoutResult.fragment();
    assertFragmentEquals(expectedMainFragment, actualMainFragment);

    Assertions.assertEquals(80, layoutResult.dimensionFrag().contentWidth());
    Assertions.assertEquals(30, layoutResult.dimensionFrag().contentHeight());

    List<LayoutFragment> expectedFloatFragments = List.of(
      new UnmanagedBoxFragment(65, 0, 15, 15, childBox1)
    );
    List<LayoutFragment> actualFloatFragments = layoutResult.rootContent().floatTracker().allFloats();
    assertFragmentListEquals(expectedFloatFragments, actualFloatFragments);
  }
  
}
