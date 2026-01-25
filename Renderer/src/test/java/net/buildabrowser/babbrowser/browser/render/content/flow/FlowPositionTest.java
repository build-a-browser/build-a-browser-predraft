package net.buildabrowser.babbrowser.browser.render.content.flow;

import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.doLayout;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class FlowPositionTest {
  
  @Test
  @DisplayName("Can layout sized block box with relative position")
  public void canLayoutSizedBlockBoxWithRelativePositon() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.POSITION, PositionValue.RELATIVE);
    childStyles.setProperty(CSSProperty.TOP, LengthValue.create(10, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.LEFT, LengthValue.create(15, true, LengthType.PX));

    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment actualFragment = doLayout(parentBox);

    LayoutFragment innerFragment = ((ManagedBoxFragment) actualFragment).fragments().get(0);
    Assertions.assertEquals(25, innerFragment.contentWidth());
    Assertions.assertEquals(25, innerFragment.contentHeight());
    Assertions.assertEquals(0, innerFragment.contentX());
    Assertions.assertEquals(0, innerFragment.contentY());
    Assertions.assertEquals(15, innerFragment.paintOffsetX());
    Assertions.assertEquals(10, innerFragment.paintOffsetY());
  }

}
