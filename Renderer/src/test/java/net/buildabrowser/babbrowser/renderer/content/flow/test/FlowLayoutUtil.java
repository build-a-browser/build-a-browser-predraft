package net.buildabrowser.babbrowser.renderer.content.flow.test;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.test.LayoutContextTestUtil;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;

public final class FlowLayoutUtil {
  
  private FlowLayoutUtil() {}

  public static ManagedBoxFragment<?> doLayout(ElementBox parentBox) {
    return doLayoutConstrained(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO).rootFragment();
  }

  public static FlowTestLayoutResult doLayoutSized(ElementBox parentBox, float width) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.AUTO);
  }

  public static FlowTestLayoutResult doLayoutSized(ElementBox parentBox, float width, float height) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.of(height));
  }

  public static FlowTestLayoutResult doLayoutConstrained(
    ElementBox parentBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    LayoutContext layoutContext = LayoutContextTestUtil.createTestLayoutContext(widthConstraint, heightConstraint);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);

    FlowRootBoxFragment dimensionFrag = (FlowRootBoxFragment) parentBox.layout(widthConstraint, heightConstraint);
    return new FlowTestLayoutResult(dimensionFrag, dimensionFrag.rootFragment());
  }

  public static record FlowTestLayoutResult(
    FlowRootBoxFragment flowFragment, ManagedBoxFragment<?> rootFragment
  ) {}

}
