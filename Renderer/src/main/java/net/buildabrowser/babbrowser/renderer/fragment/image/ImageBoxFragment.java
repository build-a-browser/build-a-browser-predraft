package net.buildabrowser.babbrowser.renderer.fragment.image;

import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class ImageBoxFragment extends UnmanagedBoxFragment<ImageBoxFragment> {

  private LoadedImage image;
  private String altText;

  public ImageBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    LoadedImage image, String altText
  ) {
    super(width, height, inkWidth, inkHeight, box);
    this.image = image;
    this.altText = altText;
  }

  public LoadedImage image() {
    return this.image;
  }

  public String altText() {
    return this.altText;
  }
  
}
