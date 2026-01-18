package net.buildabrowser.babbrowser.browser.render.content.flow.test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.test.TestFontMetrics;

public final class FlowLayoutUtil {
  
  private FlowLayoutUtil() {}

  public static FlowFragment doLayout(ElementBox parentBox) {
    LayoutContext layoutContext = new LayoutContext(TestFontMetrics.create(10, 5));
    FlowRootContent content = (FlowRootContent) parentBox.content();
    content.prelayout(layoutContext);

    content.layout(layoutContext, LayoutConstraint.AUTO, LayoutConstraint.AUTO);
    return content.rootFragment();
  }

  public static FlowFragment doLayoutSized(ElementBox parentBox, int width) {
    return doLayoutContentSized(parentBox, width).rootFragment();
  }

  public static FlowRootContent doLayoutContentSized(ElementBox parentBox, int width) {
    LayoutContext layoutContext = new LayoutContext(TestFontMetrics.create(10, 5));
    FlowRootContent content = (FlowRootContent) parentBox.content();
    content.prelayout(layoutContext);
    content.layout(layoutContext, LayoutConstraint.of(width), LayoutConstraint.AUTO);

    return content;
  }

  public static FlowFragment doLayoutSized(ElementBox parentBox, int width, int height) {
    LayoutContext layoutContext = new LayoutContext(TestFontMetrics.create(10, 5));
    FlowRootContent content = (FlowRootContent) parentBox.content();
    content.prelayout(layoutContext);
    content.layout(layoutContext, LayoutConstraint.of(width), LayoutConstraint.of(height));

    return content.rootFragment();
  }

}
