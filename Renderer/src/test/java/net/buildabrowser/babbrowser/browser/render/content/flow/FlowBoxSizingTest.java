package net.buildabrowser.babbrowser.browser.render.content.flow;

import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowBoxTestUtil.flowBlockBox;
import static net.buildabrowser.babbrowser.browser.render.content.flow.test.FlowLayoutUtil.doLayout;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;

public class FlowBoxSizingTest {
 
  @Test
  @DisplayName("Can layout sized block box with content-box sizing")
  public void canLayoutSizedBlockBoxWithContentBoxSizing() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BOX_SIZING, BoxSizingValue.CONTENT_BOX);
    setBorderStyles(childStyles, BorderStyleValue.SOLID);
    childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(5, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_LEFT_WIDTH, LengthValue.create(9, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_TOP, LengthValue.create(5, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_LEFT, LengthValue.create(6, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(40, actualFragment.contentWidth());
    Assertions.assertEquals(35, actualFragment.contentHeight());
  }
 
  @Test
  @DisplayName("Can layout sized block box with border-box sizing")
  public void canLayoutSizedBlockBoxWithBorderBoxSizing() {
    ActiveStyles childStyles = ActiveStyles.create();
    childStyles.setProperty(CSSProperty.WIDTH, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.HEIGHT, LengthValue.create(25, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BOX_SIZING, BoxSizingValue.BORDER_BOX);
    setBorderStyles(childStyles, BorderStyleValue.SOLID);
    childStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, LengthValue.create(5, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.BORDER_LEFT_WIDTH, LengthValue.create(9, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_TOP, LengthValue.create(5, true, LengthType.PX));
    childStyles.setProperty(CSSProperty.PADDING_LEFT, LengthValue.create(6, true, LengthType.PX));
    ElementBox childBox = flowBlockBox(childStyles, List.of());
    ElementBox parentBox = flowBlockBox(List.of(childBox));

    LayoutFragment actualFragment = doLayout(parentBox);
    Assertions.assertEquals(25, actualFragment.contentWidth());
    Assertions.assertEquals(25, actualFragment.contentHeight());
  }

  private void setBorderStyles(ActiveStyles childStyles, BorderStyleValue style) {
    childStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, style);
    childStyles.setProperty(CSSProperty.BORDER_BOTTOM_STYLE, style);
    childStyles.setProperty(CSSProperty.BORDER_LEFT_STYLE, style);
    childStyles.setProperty(CSSProperty.BORDER_RIGHT_STYLE, style);
  }

}
