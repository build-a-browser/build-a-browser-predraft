package net.buildabrowser.babbrowser.debugger.core;

public interface FrameDebugger {

  Debugger relatedDebugger();

  void update(DebugContext context);

  void reset();

  void detach();

  DebuggerDocumentChangeListener changeListener();

}
