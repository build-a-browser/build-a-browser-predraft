package net.buildabrowser.babbrowser.browser.render.content.flow.test;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.browser.render.layout.imp.PrelayoutStackingContextImp;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.test.TestFontMetrics;

public final class FlowLayoutUtil {
  
  private FlowLayoutUtil() {}

  public static ManagedBoxFragment doLayout(ElementBox parentBox) {
    return doLayoutConstrainted(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO).fragment();
  }

  public static FlowTestLayoutResult doLayoutSized(ElementBox parentBox, float width) {
    return doLayoutConstrainted(parentBox, LayoutConstraint.of(width), LayoutConstraint.AUTO);
  }

  public static FlowTestLayoutResult doLayoutSized(ElementBox parentBox, float width, float height) {
    return doLayoutConstrainted(parentBox, LayoutConstraint.of(width), LayoutConstraint.of(height));
  }

  public static FlowTestLayoutResult doLayoutConstrainted(
    ElementBox parentBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FontMetrics testMetrics = TestFontMetrics.create(10, 5);
    LayoutContext layoutContext = new LayoutContext(testMetrics, StackingContext.create());
    FlowRootContent content = (FlowRootContent) parentBox.content();
    // TODO: Shouldn't really pre-layout with a normal stacking context

    StackingContext plStackingContext = new PrelayoutStackingContextImp(LayoutConstraint.MIN_CONTENT);
    LayoutContext plLayoutContext = new LayoutContext(testMetrics, plStackingContext);
    content.prelayout(plLayoutContext, LayoutConstraint.MIN_CONTENT);
    plStackingContext = new PrelayoutStackingContextImp(LayoutConstraint.MAX_CONTENT);
    plLayoutContext = new LayoutContext(testMetrics, plStackingContext);
    content.prelayout(plLayoutContext, LayoutConstraint.MAX_CONTENT);

    UnmanagedBoxFragment dimensionFrag = content.layout(layoutContext, widthConstraint, heightConstraint);
    return new FlowTestLayoutResult(dimensionFrag, content.rootFragment(), content);
  }

  public static record FlowTestLayoutResult(
    UnmanagedBoxFragment dimensionFrag, ManagedBoxFragment fragment, FlowRootContent rootContent
  ) {}

}
