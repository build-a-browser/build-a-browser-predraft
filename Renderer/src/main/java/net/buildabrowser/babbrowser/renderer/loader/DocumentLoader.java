package net.buildabrowser.babbrowser.renderer.loader;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public interface DocumentLoader {

  public RenderableDocument load(
    UANavigableOptions uaNavigableOptions,
    RenderingEngine renderingEngine,
    Frame frame,
    NavigationParams navigationParams,
    SlotFamilyFamily slotFamilyFamily
  );
  
}
