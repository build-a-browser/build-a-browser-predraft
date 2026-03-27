package net.buildabrowser.babbrowser.render.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class ImageContent implements BoxContent, BoxPainter {

  private final ElementBox box;

  private URI loadingImageURL;
  private LoadedImage image;

  public ImageContent(ElementBox box) {
    this.box = box;
  }

  @Override
  public void computeIntrinsics() {
    LayoutContext layoutContext = box.layoutContext();
    loadImage(layoutContext.global());

    ElementBoxDimensions dimensions = box.dimensions();
    if (image != null) {
      // TODO: Do this the proper way
      float width = image.width();
      String widthAttr = box.element().attributes().get("width");
      if (widthAttr != null) width = Integer.valueOf(widthAttr);

      float height = image.height();
      String heightAttr = box.element().attributes().get("height");
      if (heightAttr != null) height = Integer.valueOf(heightAttr);

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
    
    return new UnmanagedBoxFragment(realWidth, realHeight, box, this);
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().backgroundColor()));
    float width = fragment.contentWidth();
    float height = fragment.contentHeight();
    canvas.drawBox(0, 0, width, height);
    canvas.alterPaint(paint -> {
      paint.setFont(box.layoutContext().font());
      paint.setColor(box.activeStyles().textColor());
    });

    if (image != null) {
      canvas.drawImage(0, 0, width, height, image);
      return;
    }

    String alt = getImageAlt();
    canvas.drawText(0, 0, alt);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    // TODO: Implement
  }

  @Override
  public boolean isReplaced() {
    return true;
  };

  private void loadImage(GlobalLayoutContext layoutContext) {
    URI imageSource = getImageSource(layoutContext.refURL());
    if (imageSource == null) return;
    if (loadingImageURL == null || !loadingImageURL.equals(imageSource)) {
      image = null;
      loadingImageURL = imageSource;
      
      MutableFetchRequest fetchRequest = FetchRequest.createMutable();
      fetchRequest.setMethod("GET");
      fetchRequest.setURL(imageSource);
      fetchRequest.setClient(layoutContext.scriptingContext().environmentSettingsObject());

      FetchParameters fetchParameters = new FetchParameters();
      fetchParameters.request = fetchRequest;
      fetchParameters.processResponseConsumeBody = (response, success, bytes) -> {
        if (success) {
          GlobalObject globalObject = layoutContext.scriptingContext().environmentSettingsObject().globalObject();
          EventLoop.queueGlobalTask(TaskSource.DOM, globalObject, () -> loadBufferedImage(layoutContext, bytes));
        }
      };

      layoutContext.scriptingContext().fetchEngine().fetch(fetchParameters);
    }
  }

  private synchronized void loadBufferedImage(GlobalLayoutContext layoutContext, byte[] bytes) {
    try {
      // TODO: Also need to handle SVG
      this.image = layoutContext.resourceLoader().loadImage(new ByteArrayInputStream(bytes));
      box.element().invalidate(InvalidationLevel.LAYOUT);
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
      this.image = null;
    }
  }

  private URI getImageSource(URI refUrl) {
    String src = box.element().attributes().get("src");
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
    String alt = box.element().attributes().get("alt");
    if (alt == null) {
      return "Image";
    }
    return alt;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    // No-op
  }
  
}
