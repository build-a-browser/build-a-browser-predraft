package net.buildabrowser.babbrowser.browser.render.content;

import java.io.IOException;
import java.net.URI;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedImage;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class ImageContent implements BoxContent, BoxPainter {

  private final ElementBox box;

  private URI loadingImageURL;
  private LoadedImage image;

  public ImageContent(ElementBox box) {
    this.box = box;
  }

  @Override
  public void computeIntrinsics(LayoutContext layoutContext) {
    loadImage(layoutContext.global());

    ElementBoxDimensions dimensions = box.dimensions();
    if (image != null) {
      dimensions.setIntrinsicWidth(image.width());
      dimensions.setInstrinsicHeight(image.height());
      dimensions.setIntrinsicRatio((float) image.width() / (float) image.height());
      return;
    }

    String alt = getImageAlt();
    FontMetrics fm = layoutContext.fontMetrics();
    
    float width = fm.stringWidth(alt);
    
    dimensions.setIntrinsicWidth(width);
    dimensions.setInstrinsicHeight(fm.fontHeight());
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
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
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().textColor()));

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
    if (loadingImageURL == null || !loadingImageURL.equals(imageSource)) {
      image = null;
      loadingImageURL = imageSource;
      new Thread(() -> loadBufferedImage(layoutContext, loadingImageURL)).start();
    }
  }

  private synchronized void loadBufferedImage(GlobalLayoutContext layoutContext, URI loadingImageURL) {
    try {
      this.image = layoutContext.resourceLoader().loadImage(loadingImageURL.toURL().openStream());
      box.invalidate(InvalidationLevel.LAYOUT);
    } catch (IOException e) {
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
  
}
