package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.input.FocusOptions;

public interface HTMLOrSVGOrMathMLElement extends Node {
  
  long tabIndex();

  void focus(FocusOptions options);

}
