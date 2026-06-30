package net.buildabrowser.babbrowser.a11y.core;

import java.io.IOException;

public interface A11YProvider {
  
  A11YFrame createFrame(A11YOps ops) throws IOException;

}
