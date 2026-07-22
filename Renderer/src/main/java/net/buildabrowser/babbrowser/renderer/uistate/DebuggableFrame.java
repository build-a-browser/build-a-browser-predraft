package net.buildabrowser.babbrowser.renderer.uistate;

import net.buildabrowser.babbrowser.debugger.core.Debugger;

public interface DebuggableFrame extends Frame {
  
  void attachDebugger(Debugger debugger);
  
  void detachDebugger(Debugger debugger);

}
