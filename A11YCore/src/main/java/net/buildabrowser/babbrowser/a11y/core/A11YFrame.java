package net.buildabrowser.babbrowser.a11y.core;

import java.io.Closeable;

import net.buildabrowser.babbrowser.dom.Node;

public interface A11YFrame extends Closeable {
  
  void update(Node node);

}
