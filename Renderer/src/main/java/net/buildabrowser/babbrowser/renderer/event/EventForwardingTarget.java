package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public interface EventForwardingTarget {
  
  default void forwardEvent(RendererMouseEvent event) {}

  default void forwardEvent(RendererKeyboardEvent event) {}

}
