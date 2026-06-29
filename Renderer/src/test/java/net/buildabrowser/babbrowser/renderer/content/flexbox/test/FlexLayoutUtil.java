package net.buildabrowser.babbrowser.renderer.content.flexbox.test;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.renderer.paint.test.TestResourceLoader;

public final class FlexLayoutUtil {
  
  private FlexLayoutUtil() {}

  public static UnmanagedBoxFragment<?> doLayout(ElementBox parentBox) {
    return doLayoutConstrained(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO).childFragments();
  }

  public static FlexTestLayoutResult doLayoutSized(ElementBox parentBox, float width) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.AUTO);
  }

  public static FlexTestLayoutResult doLayoutSized(ElementBox parentBox, float width, float height) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.of(height));
  }

  public static FlexTestLayoutResult doLayoutConstrained(
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
        (m, s) -> m.stringWidth(s),
        viewport, null, null, fragmentFactory),
      () -> testMetrics);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);
    FlexBoxContent content = (FlexBoxContent) parentBox.content();

    content.fixupChildren(parentBox);
    FlexBoxFragment dimensionFrag = (FlexBoxFragment) parentBox.layout(widthConstraint, heightConstraint);
    return new FlexTestLayoutResult(dimensionFrag, dimensionFrag.fragments(), content);
  }

  public static record FlexTestLayoutResult(
    FlexBoxFragment flexboxFragment, UnmanagedBoxFragment<?> childFragments, FlexBoxContent rootContent
  ) {}

}
