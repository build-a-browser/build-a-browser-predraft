package net.buildabrowser.babbrowser.renderer.fragment.image;

import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.image.ImageEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.image.ImageBoxPainter;

public class DefaultImageBoxFragment extends ImageBoxFragment {

  private static final ImageBoxPainter IMAGE_BOX_PAINTER = new ImageBoxPainter();
  private static final ImageEventHandler IMAGE_EVENT_HANDLER = new ImageEventHandler();

  public DefaultImageBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    LoadedImage image, String altText
  ) {
    super( 
      width, height, inkWidth, inkHeight,
      box, image, altText);
  }

  @Override
  protected BoxPainter<ImageBoxFragment> painter() {
    return IMAGE_BOX_PAINTER;
  }

  @Override
  protected EventHandler<ImageBoxFragment> eventHandler() {
    return IMAGE_EVENT_HANDLER;
  }
  
}
