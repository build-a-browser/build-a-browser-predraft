package net.buildabrowser.babbrowser.render.event;

import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public interface EventForwardingTarget {
  
  void forwardEvent(RendererMouseEvent mouseEvent);

}
