package net.buildabrowser.babbrowser.browser.render.content.flow;

import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.doLayout;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
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

}
