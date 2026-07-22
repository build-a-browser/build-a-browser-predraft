package net.buildabrowser.babbrowser.debugger.core;

import java.util.List;

public interface DebugLayer {

  List<DebugLayer> childLayers();
  
  DebugRect layerDocRect();

  DebugRect layerVpRect();

}
