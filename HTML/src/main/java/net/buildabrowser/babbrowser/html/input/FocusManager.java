package net.buildabrowser.babbrowser.html.input;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.input.imp.FocusManagerImp;

public interface FocusManager {
  
  Node focused();

  FocusOptions focusOptions();

  void focus(Node node, FocusOptions options);

  void focusNext(FocusOptions options);

  void focusPrevious(FocusOptions options);

  void unfocus();

  // TODO: Better way to do this...
  void attachContext(FocusManagerContext context);

  static FocusManager create(Document document) {
    return new FocusManagerImp(document);
  }

}
