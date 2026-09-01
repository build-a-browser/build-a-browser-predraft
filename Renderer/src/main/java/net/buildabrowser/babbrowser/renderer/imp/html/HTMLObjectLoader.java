package net.buildabrowser.babbrowser.renderer.imp.html;

import java.util.function.Supplier;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement;
import net.buildabrowser.babbrowser.html.html.HTMLObjectElement.ObjectRepresentation;
import net.buildabrowser.babbrowser.html.html.handlers.ObjectLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.content.image.ImageContent;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;

public class HTMLObjectLoader implements ObjectLoader {

  private final ImageCache imageCache;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public HTMLObjectLoader(
    ImageCache imageCache,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    this.imageCache = imageCache;
    this.renderContexts = renderContexts;
  }

  @Override
  public boolean supportsMimeType(String mimeType) {
    return mimeType.startsWith("image/");
  }

  @Override
  public ObjectRepresentation load(
    FetchResponse response,
    HTMLObjectElement element
  ) {
    Invalidatable invalidatable = renderContexts.get(element);
    return new ImageObjectRepresentation(() -> imageCache.getImage(
      response, invalidatable, InvalidationLevel.BOX));
  }

  public static BoxContent createContent(HTMLObjectElement element) {
    // TODO: More types
    if (
      element.representation() instanceof ImageObjectRepresentation ioRep
      && ioRep.imageSupplier().get() instanceof LoadedImage loadedImage
    ) {
      // TODO: But if there is now a loaded image, and there was not before, children
      // navigable need removed
      return new ImageContent(loadedImage);
    } else {
      return null;
    }
  }

  public static record ImageObjectRepresentation(
    Supplier<LoadedImage> imageSupplier
  ) implements ObjectRepresentation {}

}
