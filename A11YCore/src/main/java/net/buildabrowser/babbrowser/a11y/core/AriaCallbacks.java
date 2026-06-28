package net.buildabrowser.babbrowser.a11y.core;

public interface AriaCallbacks<T> {
  
  T visitNode(T parent, long nodeId, AriaRole role);

  void visitAttribute(T node, AriaProperty property, String value);

  void onNodeEvent(long nodeId, AriaEvent event);

  void onFocusChanged(long focusId);

}
