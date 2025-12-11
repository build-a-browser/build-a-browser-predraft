package net.buildabrowser.babbrowser.browser.render.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;
import net.buildabrowser.babbrowser.browser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class ImageContent implements BoxContent {

  private final ElementBox box;

  private URL loadingImageURL;
  private BufferedImage image;

  public ImageContent(ElementBox box) {
    this.box = box;
  }

  @Override
  public void layout(LayoutContext layoutContext) {
    loadImage();

    if (image != null) {
      box.dimensions().setComputedSize(image.getWidth(), image.getHeight());
      return;
    }

    String alt = getImageAlt();
    FontMetrics fm = layoutContext.fontMetrics();
    
    int width = fm.stringWidth(alt);
    int height = fm.fontHeight();
    
    box.dimensions().setComputedSize(width, height);
  }

  @Override
  public void paint(PaintCanvas canvas) {
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().backgroundColor()));
    int width = box.dimensions().getComputedWidth();
    int height = box.dimensions().getComputedHeight();
    canvas.drawBox(0, 0, width, height);
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().textColor()));

    if (image != null) {
      canvas.drawImage(0, 0, image);
      return;
    }

    String alt = getImageAlt();
    canvas.drawText(0, 0, alt);
  }

  private void loadImage() {
    URL imageSource = getImageSource();
    if (loadingImageURL == null || !loadingImageURL.equals(imageSource)) {
      image = null;
      loadingImageURL = imageSource;
      new Thread(() -> loadBufferedImage(loadingImageURL)).start();
    }
  }

  private synchronized void loadBufferedImage(URL loadingImageURL) {
    try {
      this.image = ImageIO.read(loadingImageURL);
      box.invalidate(InvalidationLevel.LAYOUT);
    } catch (IOException e) {
      e.printStackTrace();
      this.image = null;
    }
  }

  private URL getImageSource() {
    String src = box.element().attributes().get("src");
    if (src == null || src.isEmpty()) {
      return null;
    }

    try {
      return URLUtil.createURL(src);
    } catch (BadURLException e) {
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
