package net.buildabrowser.babbrowser.render.content.flow.test;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;
import net.buildabrowser.babbrowser.render.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.render.paint.test.TestResourceLoader;

public final class FlowLayoutUtil {
  
  private FlowLayoutUtil() {}

  public static ManagedBoxFragment doLayout(ElementBox parentBox) {
    return doLayoutConstrained(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO).fragment();
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
    FontMetrics testMetrics = TestFontMetrics.create(10, 5);
    ResourceLoader resourceLoader = new TestResourceLoader(() -> testMetrics);
    LayoutContext layoutContext = new LayoutContext(
      new GlobalLayoutContext(
        null, resourceLoader, testMetrics, resourceLoader.fontLoader()::load,
        null, new Object()),
      () -> testMetrics);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);
    FlowRootContent content = (FlowRootContent) parentBox.content();

    UnmanagedBoxFragment dimensionFrag = parentBox.layout(widthConstraint, heightConstraint);
    return new FlowTestLayoutResult(dimensionFrag, content.rootFragment(), content);
  }

  public static record FlowTestLayoutResult(
    UnmanagedBoxFragment dimensionFrag, ManagedBoxFragment fragment, FlowRootContent rootContent
  ) {}

}
