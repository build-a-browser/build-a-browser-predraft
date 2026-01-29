package net.buildabrowser.babbrowser.browser.render.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;
import net.buildabrowser.babbrowser.browser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class ImageContent implements BoxContent, BoxPainter {

  private final ElementBox box;

  private URL loadingImageURL;
  private BufferedImage image;

  public ImageContent(ElementBox box) {
    this.box = box;
  }

  @Override
  public void prelayout(LayoutContext layoutContext) {
    loadImage(layoutContext.refURL());

    ElementBoxDimensions dimensions = box.dimensions();
    if (image != null) {
      dimensions.setPreferredMinWidthConstraint(image.getWidth());
      dimensions.setPreferredWidthConstraint(image.getWidth());
      dimensions.setIntrinsicWidth(image.getWidth());
      dimensions.setInstrinsicHeight(image.getHeight());
      dimensions.setIntrinsicRatio((float) image.getWidth() / (float) image.getHeight());
      return;
    }

    String alt = getImageAlt();
    FontMetrics fm = layoutContext.fontMetrics();
    
    int width = fm.stringWidth(alt);
    
    dimensions.setPreferredMinWidthConstraint(width);
    dimensions.setPreferredWidthConstraint(width);
    dimensions.setIntrinsicWidth(width);
    dimensions.setInstrinsicHeight(fm.fontHeight());
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = box.dimensions();
    int realWidth = LayoutUtil.constraintOrDim(widthConstraint, dimensions.intrinsicWidth());
    int realHeight = LayoutUtil.constraintOrDim(heightConstraint, dimensions.intrinsicHeight());
    
    return new UnmanagedBoxFragment(realWidth, realHeight, box, this);
  }

  @Override
  public void layer(CompositeLayer layer) {
    // No-op
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().backgroundColor()));
    int width = fragment.contentWidth();
    int height = fragment.contentHeight();
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

  private void loadImage(URL refURL) {
    URL imageSource = getImageSource(refURL);
    if (loadingImageURL == null || !loadingImageURL.equals(imageSource)) {
      image = null;
      loadingImageURL = imageSource;
      new Thread(() -> loadBufferedImage(loadingImageURL)).start();
    }
  }

  private synchronized void loadBufferedImage(URL loadingImageURL) {
    try {
      this.image = ImageIO.read(loadingImageURL.toURI().toURL());
      box.invalidate(InvalidationLevel.LAYOUT);
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      this.image = null;
    }
  }

  private URL getImageSource(URL refUrl) {
    String src = box.element().attributes().get("src");
    if (src == null || src.isEmpty()) {
      return null;
    }

    try {
      return URLUtil.createURL(refUrl, src);
    } catch (BadURLException | IllegalArgumentException e) {
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
