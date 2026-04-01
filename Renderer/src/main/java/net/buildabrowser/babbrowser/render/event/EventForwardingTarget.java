package net.buildabrowser.babbrowser.render.event;

import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent;

public interface EventForwardingTarget {
  
  void forwardEvent(MouseEvent mouseEvent);

}
