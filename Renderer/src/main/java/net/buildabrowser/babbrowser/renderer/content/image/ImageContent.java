package net.buildabrowser.babbrowser.renderer.content.image;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.image.ImageBoxFragment;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class ImageContent implements BoxContent {

  private final ElementBox rootBox;

  private LoadedImage image;

  public ImageContent(ElementBox box) {
    this.rootBox = box;
  }

  @Override
  public void computeIntrinsics() {
    LayoutContext layoutContext = rootBox.layoutContext();
    this.image = loadImage(layoutContext.global());

    if (image != null) {
      float width = image.width();
      float height = image.height();

      rootBox.alterDimensions(false, dimensions -> {
        dimensions.setIntrinsicWidth(width);
        dimensions.setInstrinsicHeight(height);
        dimensions.setIntrinsicRatio((float) image.width() / (float) image.height());
      });
      return;
    }

    String alt = getImageAlt();
    FontMetrics fm = layoutContext.font().metrics();
    
    float width = fm.stringWidth(alt);
    
    rootBox.alterDimensions(false, dimensions -> {
      dimensions.setIntrinsicWidth(width);
      dimensions.setInstrinsicHeight(fm.height());
    });
  }

  @Override
  public ImageBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = rootBox.dimensions();
    float realWidth = LayoutUtil.constraintOrDim(widthConstraint, dimensions.intrinsicWidth());
    float realHeight = LayoutUtil.constraintOrDim(heightConstraint, dimensions.intrinsicHeight());
    
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createImageBoxFragment(
      realWidth, realHeight, realWidth,
      realHeight, rootBox, image, getImageAlt());
  }

  @Override
  public boolean isReplaced() {
    return true;
  }

  private LoadedImage loadImage(GlobalLayoutContext layoutContext) {
    Document nodeDocument = rootBox.element().nodeDocument();
    if (!(nodeDocument instanceof HTMLDocument htmlDocument)) return null;
    URI baseURL = htmlDocument.baseURL();
    URI imageSource = getImageSource(baseURL);
    if (imageSource == null) return null;
    ImageCache imageCache = layoutContext.imageCache();
    return imageCache.getImage(imageSource, rootBox.context(), InvalidationLevel.LAYOUT);
  }

  private URI getImageSource(URI refUrl) {
    String src = rootBox.element().getAttribute("src");
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
    String alt = rootBox.element().getAttribute("alt");
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
  public ElementBox rootBox() {
    return this.rootBox;
  }
  
}
