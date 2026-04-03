package net.buildabrowser.babbrowser.dom.events;

public interface EventTarget {
 
  default EventTarget getTheParent() {
    return null;
  }

}
