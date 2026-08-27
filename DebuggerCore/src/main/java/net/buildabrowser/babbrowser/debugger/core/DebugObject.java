package net.buildabrowser.babbrowser.debugger.core;

public interface DebugObject {

  DebugSnapshot snapshotDebugInfo();

  void markSelection(DebugObjectSelection selection);

  enum DebugObjectSelection {
    NONE, SELECTED, TARGETED;
  }
  
}
