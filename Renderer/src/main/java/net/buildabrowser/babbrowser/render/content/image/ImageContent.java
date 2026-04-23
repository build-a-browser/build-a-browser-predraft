package net.buildabrowser.babbrowser.render.content.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class ImageContent implements BoxContent, BoxPainter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageContent.class);

  private static final EventHandler EVENT_HANDLER = new ImageEventHandler();

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
      String widthAttr = box.element().getAttribute("width");
      if (widthAttr != null) width = Integer.valueOf(widthAttr);

      float height = image.height();
      String heightAttr = box.element().getAttribute("height");
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
    
    return new UnmanagedBoxFragment(realWidth, realHeight, realWidth, realHeight, box, this);
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
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
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    // TODO: Implement
  }

  @Override
  public boolean isReplaced() {
    return true;
  };

  private void loadImage(GlobalLayoutContext layoutContext) {
    Document nodeDocument = box.element().nodeDocument();
    URI baseURL = nodeDocument instanceof HTMLDocument htmlDocument ?
      htmlDocument.baseURL() :
      nodeDocument.url();
    URI imageSource = getImageSource(baseURL);
    if (imageSource == null) return;
    if (loadingImageURL == null || !loadingImageURL.equals(imageSource)) {
      image = null;
      loadingImageURL = imageSource;
      
      MutableFetchRequest fetchRequest = FetchRequest.createMutable();
      fetchRequest.setMethod("GET");
      fetchRequest.appendURL(imageSource);
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
      LOGGER.error("An error occured while loading the image!", e);
      this.image = null;
    }
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
