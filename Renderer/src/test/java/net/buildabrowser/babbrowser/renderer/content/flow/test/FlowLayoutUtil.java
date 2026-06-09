package net.buildabrowser.babbrowser.renderer.content.flow.test;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.renderer.paint.test.TestResourceLoader;

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
    FontMetrics testMetrics = TestFontMetrics.create(10, 5);
    ResourceLoader resourceLoader = new TestResourceLoader(() -> testMetrics);
    Viewport viewport = new Viewport(0, 0, (int) widthConstraint.value(), (int) heightConstraint.value());
    FragmentFactory fragmentFactory = FragmentFactory.createDefault();
    LayoutContext layoutContext = new LayoutContext(
      new GlobalLayoutContext(
        resourceLoader, testMetrics, resourceLoader.fontLoader()::load,
        viewport, null, null, fragmentFactory),
      () -> testMetrics);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);

    FlowRootBoxFragment dimensionFrag = (FlowRootBoxFragment) parentBox.layout(widthConstraint, heightConstraint);
    return new FlowTestLayoutResult(dimensionFrag, dimensionFrag.rootFragment());
  }

  public static record FlowTestLayoutResult(
    FlowRootBoxFragment flowFragment, ManagedBoxFragment<?> rootFragment
  ) {}

}
