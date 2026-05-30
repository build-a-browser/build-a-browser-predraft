package net.buildabrowser.babbrowser.render.loader;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.render.paint.backend.Painter;

public interface DocumentLoader {

  public RenderableDocument load(
    UANavigableOptions uaNavigableOptions,
    Painter painter,
    NavigationParams navigationParams
  );
  
}
