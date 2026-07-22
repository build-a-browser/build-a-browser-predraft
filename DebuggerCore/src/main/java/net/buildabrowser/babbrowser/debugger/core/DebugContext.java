package net.buildabrowser.babbrowser.debugger.core;

import net.buildabrowser.babbrowser.dom.Node;

public interface DebugContext {

  Node rootNode();
  
  DebugLayer rootLayer();

}
