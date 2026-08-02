package net.buildabrowser.babbrowser.renderer.content.common.test;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.renderer.paint.test.TestResourceLoader;

public final class LayoutContextTestUtil {
  
  private LayoutContextTestUtil() {}

  public static LayoutContext createTestLayoutContext(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    FontMetrics testMetrics = TestFontMetrics.create(10, 5);
    ResourceLoader resourceLoader = new TestResourceLoader(() -> testMetrics);
    Viewport viewport = new Viewport(0, 0, (int) widthConstraint.value(), (int) heightConstraint.value());
    FragmentFactory fragmentFactory = FragmentFactory.createDefault();
    LayoutContext layoutContext = new LayoutContext(
      new GlobalLayoutContext(
        resourceLoader, testMetrics, resourceLoader.fontLoader()::load,
        (m, s) -> m.stringWidth(s),
        viewport, null, null, null, fragmentFactory),
      () -> testMetrics);
    return layoutContext;
  }

}
