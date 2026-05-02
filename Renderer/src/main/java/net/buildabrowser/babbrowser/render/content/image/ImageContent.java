package net.buildabrowser.babbrowser.render.content.image;

import java.net.URI;

import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.image.ImageCache;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class ImageContent implements BoxContent, BoxPainter {

  private static final EventHandler EVENT_HANDLER = new ImageEventHandler();

  private final ElementBox box;

  private LoadedImage image;

  public ImageContent(ElementBox box) {
    this.box = box;
  }

  @Override
  public void computeIntrinsics() {
    LayoutContext layoutContext = box.layoutContext();
    this.image = loadImage(layoutContext.global());

    ElementBoxDimensions dimensions = box.dimensions();
    if (image != null) {
      // TODO: Do this the proper way
      float width = image.width();
      String widthAttr = box.element().getAttribute("width");
      try {
        if (widthAttr != null) width = Integer.valueOf(widthAttr);
      } catch (NumberFormatException e) {}

      float height = image.height();
      String heightAttr = box.element().getAttribute("height");
      try {
        if (heightAttr != null) height = Integer.valueOf(heightAttr);
      } catch (NumberFormatException e) {}

      dimensions.setIntrinsicWidth(width);
      dimensions.setInstrinsicHeight(height);
      dimensions.setIntrinsicRatio((float) image.width() / (float) image.height());
      return;
    }

    String alt = getImageAlt();
    FontMetrics fm = layoutContext.font().metrics();
    
    float width = fm.stringWidth(alt);
    
    dimensions.setIntrinsicWidth(width);
    dimensions.setInstrinsicHeight(fm.height());
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = box.dimensions();
    float realWidth = LayoutUtil.constraintOrDim(widthConstraint, dimensions.intrinsicWidth());
    float realHeight = LayoutUtil.constraintOrDim(heightConstraint, dimensions.intrinsicHeight());
    
    return new UnmanagedBoxFragment(realWidth, realHeight, realWidth, realHeight, box, this);
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    canvas.alterPaint(paint -> paint.setColor(ActiveStylesUtil.backgroundColor(box.activeStyles())));
    float width = fragment.width(Measurement.CONTENT);
    float height = fragment.height(Measurement.CONTENT);
    canvas.drawBox(0, 0, width, height);
    canvas.alterPaint(paint -> {
      paint.setFont(box.layoutContext().font());
      paint.setColor(ActiveStylesUtil.textColor(box.activeStyles()));
    });

    if (image != null) {
      canvas.drawImage(0, 0, width, height, image);
      return;
    }

    String alt = getImageAlt();
    canvas.drawText(0, 0, alt);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    // TODO: Implement
  }

  @Override
  public boolean isReplaced() {
    return true;
  }

  private LoadedImage loadImage(GlobalLayoutContext layoutContext) {
    Document nodeDocument = box.element().nodeDocument();
    if (!(nodeDocument instanceof HTMLDocument htmlDocument)) return null;
    URI baseURL = htmlDocument.baseURL();
    URI imageSource = getImageSource(baseURL);
    if (imageSource == null) return null;
    ImageCache imageCache = layoutContext.imageCache();
    return imageCache.getImage(imageSource, box.element(), InvalidationLevel.LAYOUT);
  }

  private URI getImageSource(URI refUrl) {
    String src = box.element().getAttribute("src");
    if (src == null || src.isEmpty()) {
      return null;
    }

    try {
      return URLUtil.createURL(refUrl, src);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private String getImageAlt() {
    String alt = box.element().getAttribute("alt");
    if (alt == null) {
      return "Image";
    }
    return alt;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    // No-op
  }

  @Override
  public EventHandler eventHandler() {
    return EVENT_HANDLER;
  }

  @Override
  public ElementBox rootBox() {
    return this.box;
  }
  
}
