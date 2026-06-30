package net.buildabrowser.babbrowser.a11y.core;

import net.buildabrowser.babbrowser.a11y.core.aom.AriaEvent;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaProperty;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;

public interface AriaCallbacks<T> {
  
  T visitNode(T parent, long nodeId, AriaRole role);

  void exitNode(T node, long nodeId);

  void visitAttribute(T node, AriaProperty property, String value);

  void visitText(T node, String value);

  void onNodeEvent(long nodeId, AriaEvent event);

  void onFocusChanged(long focusId);

}
