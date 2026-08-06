package net.buildabrowser.babbrowser.dom.events;

public interface FocusEvent extends UIEvent {

  static FocusEvent create(String name) {
    return () -> name;
  }
  
}
