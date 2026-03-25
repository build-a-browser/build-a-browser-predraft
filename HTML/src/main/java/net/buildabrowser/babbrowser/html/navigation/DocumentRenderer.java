package net.buildabrowser.babbrowser.html.navigation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

public interface DocumentRenderer {

  void start() throws IOException;

  void shutdown();
  
  boolean shouldRender();

  void recalculateStyles();

  void updateLayout();

  void updateRendering();

  void resize(int width, int height);

  void withImage(Consumer<BufferedImage> func);

}
