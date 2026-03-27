package net.buildabrowser.babbrowser.render.content.flexbox.test;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;
import net.buildabrowser.babbrowser.render.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.render.paint.test.TestResourceLoader;

public final class FlexLayoutUtil {
  
  private FlexLayoutUtil() {}

  public static UnmanagedBoxFragment doLayout(ElementBox parentBox) {
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
    LayoutContext layoutContext = new LayoutContext(
      new GlobalLayoutContext(
        null, resourceLoader, testMetrics, resourceLoader.fontLoader()::load,
        null, new Object()),
      () -> testMetrics);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);
    FlexBoxContent content = (FlexBoxContent) parentBox.content();

    content.fixupChildren();
    UnmanagedBoxFragment dimensionFrag = parentBox.layout(widthConstraint, heightConstraint);
    return new FlexTestLayoutResult(dimensionFrag, content.fragments(), content);
  }

  public static record FlexTestLayoutResult(
    UnmanagedBoxFragment dimensionFrag, UnmanagedBoxFragment childFragments, FlexBoxContent rootContent
  ) {}

}
