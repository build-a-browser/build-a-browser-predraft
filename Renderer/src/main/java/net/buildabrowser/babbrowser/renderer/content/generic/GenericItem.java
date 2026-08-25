package net.buildabrowser.babbrowser.renderer.content.generic;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public interface GenericItem {

  ElementBox box();
  
  UnmanagedBoxFragment<?> fragment();

  float crossSize();

  void setCrossPos(float itemCrossPos, boolean isVertical);

}
