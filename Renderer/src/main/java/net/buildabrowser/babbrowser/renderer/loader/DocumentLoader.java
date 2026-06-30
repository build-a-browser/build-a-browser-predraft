package net.buildabrowser.babbrowser.renderer.loader;

import java.io.IOException;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.painter.core.Painter;

public interface DocumentLoader {

  public RenderableDocument load(
    UANavigableOptions uaNavigableOptions,
    Painter painter,
    A11YProvider a11yProvider,
    NavigationParams navigationParams,
    SlotFamilyFamily slotFamilyFamily
  ) throws IOException;
  
}
