package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public interface EventForwardingTarget {
  
  void forwardEvent(RendererMouseEvent mouseEvent);

}
