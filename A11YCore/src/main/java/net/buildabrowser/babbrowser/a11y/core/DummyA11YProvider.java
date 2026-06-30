package net.buildabrowser.babbrowser.a11y.core;

import java.io.IOException;

public class DummyA11YProvider implements A11YProvider {

  @Override
  public A11YFrame createFrame(A11YOps ops) throws IOException {
    return new DummyA11YFrame();
  }
  
}
