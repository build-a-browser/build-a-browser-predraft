package net.buildabrowser.babbrowser.a11y.core;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public interface A11YOps {
  
  boolean isSkipped(Node node);

  boolean isIgnored(Node node);

  boolean hasSemanticMeaning(Element element);

}
