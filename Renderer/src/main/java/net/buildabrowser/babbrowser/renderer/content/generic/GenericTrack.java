package net.buildabrowser.babbrowser.renderer.content.generic;

import java.util.List;

public interface GenericTrack {

  List<GenericItem> genericItems();

  float crossSize();

  void setCrossPos(float startPos, boolean isVertical);
  
}
