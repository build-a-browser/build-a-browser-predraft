package net.buildabrowser.babbrowser.render;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.render.image.ImageCache;

public interface HTMLDocumentRenderer extends DocumentRenderer {
  
  // TODO: It is a bit of a hack to put this here
  ImageCache imageCache();

}
