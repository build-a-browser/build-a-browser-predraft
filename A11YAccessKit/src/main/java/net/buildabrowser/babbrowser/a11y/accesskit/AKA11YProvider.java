package net.buildabrowser.babbrowser.a11y.accesskit;

import java.io.IOException;

import net.buildabrowser.babbrowser.a11y.core.A11YFrame;
import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.a11y.core.A11YProvider;

public class AKA11YProvider implements A11YProvider {

  @Override
  public A11YFrame createFrame(A11YOps ops) throws IOException {
    return new AKA11YFrame(ops);
  }
  
}
