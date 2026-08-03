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
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.image.ImageBoxFragment;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public class ImageContent implements BoxContent {

  private String imageAlt;
  private URI imageSource;
  private LoadedImage image;

  @Override
  public void computeIntrinsics(ElementBox rootBox) {
    this.imageAlt = getImageAlt(rootBox);
    this.imageSource = getImageSource(rootBox);
    if (imageSource != null) {
      this.image = loadImage(rootBox, imageSource);
    }

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

    LayoutContext layoutContext = rootBox.layoutContext();
    FontMetrics fm = layoutContext.font().metrics();
    
    float width = fm.stringWidth(imageAlt);
    
    rootBox.alterDimensions(false, dimensions -> {
      dimensions.setIntrinsicWidth(width);
      dimensions.setInstrinsicHeight(fm.height());
    });
  }

  @Override
  public ImageBoxFragment layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = rootBox.dimensions();
    float realWidth =
      widthConstraint.isBounded() ? widthConstraint.floatValue() :
      heightConstraint.isBounded() && image != null ? dimensions.intrinsicRatio() * heightConstraint.value() :
      dimensions.intrinsicWidth();
    float realHeight =
      heightConstraint.isBounded() ? heightConstraint.floatValue() :
      widthConstraint.isBounded() && image != null ? widthConstraint.value() / dimensions.intrinsicRatio() :
      dimensions.intrinsicHeight();
    
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createImageBoxFragment(
      realWidth, realHeight, realWidth,
      realHeight, rootBox, image, getImageAlt(rootBox));
  }

  @Override
  public boolean isReplaced(ElementBox box) {
    return true;
  }

  public String alt() {
    return this.imageAlt;
  }

  public URI imageSource() {
    return this.imageSource;
  }

  public LoadedImage loadedImage() {
    return this.image;
  }

  private LoadedImage loadImage(ElementBox rootBox, URI imageSource) {
    GlobalLayoutContext layoutContext = rootBox.layoutContext().global();
    ImageCache imageCache = layoutContext.imageCache();
    return imageCache.getImage(imageSource, rootBox.context(), InvalidationLevel.LAYOUT);
  }

  private URI getImageSource(ElementBox rootBox) {
    Document nodeDocument = rootBox.element().nodeDocument();
    if (!(nodeDocument instanceof HTMLDocument htmlDocument)) return null;
    URI baseURL = htmlDocument.baseURL();

    String src = rootBox.element().getAttribute("src");
    if (src == null || src.isEmpty()) {
      return null;
    }

    try {
      return URLUtil.createURL(baseURL, src);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private String getImageAlt(ElementBox rootBox) {
    String alt = rootBox.element().getAttribute("alt");
    if (alt == null) {
      return "Image";
    }
    return alt;
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    // No-op
  }
  
}
