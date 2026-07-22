package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;

public record GlobalLayoutContext(
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  FontCache fontCache,
  FontWordWidthCache fontWordWidthCache,
  Viewport viewport,
  ScriptingContext scriptingContext,
  SelectionContext selectionContext,
  ImageCache imageCache,
  FragmentFactory fragmentFactory
) {
  
}
