package net.buildabrowser.babbrowser.browser.render.content.flow.test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.browser.render.paint.test.TestFontMetrics;

public final class FlowLayoutUtil {
  
  private FlowLayoutUtil() {}

  public static ManagedBoxFragment doLayout(ElementBox parentBox) {
    return doLayoutConstrainted(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO).fragment();
  }

  public static FlowTestLayoutResult doLayoutSized(ElementBox parentBox, int width) {
    return doLayoutConstrainted(parentBox, LayoutConstraint.of(width), LayoutConstraint.AUTO);
  }

  public static FlowTestLayoutResult doLayoutSized(ElementBox parentBox, int width, int height) {
    return doLayoutConstrainted(parentBox, LayoutConstraint.of(width), LayoutConstraint.of(height));
  }

  public static FlowTestLayoutResult doLayoutConstrainted(
    ElementBox parentBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    LayoutContext layoutContext = new LayoutContext(
      TestFontMetrics.create(10, 5),
      StackingContext.create());
    FlowRootContent content = (FlowRootContent) parentBox.content();
    // TODO: Shouldn't really pre-layout with a normal stacking context
    content.prelayout(layoutContext);

    UnmanagedBoxFragment dimensionFrag = content.layout(layoutContext, widthConstraint, heightConstraint);
    return new FlowTestLayoutResult(dimensionFrag, content.rootFragment(), content);
  }

  public static record FlowTestLayoutResult(
    UnmanagedBoxFragment dimensionFrag, ManagedBoxFragment fragment, FlowRootContent rootContent
  ) {}

}
