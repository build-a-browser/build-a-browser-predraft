package net.buildabrowser.babbrowser.htmlparser.insertion;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.htmlparser.insertion.imp.OpenElementStackImp;

public interface OpenElementStack {
  
  void pushNode(Node node);

  Node peek();

  Node peek(int pos);

  Node popNode();

  void removeSpecificNode(Node node);

  int size();

  static OpenElementStack create() {
    return new OpenElementStackImp();
  }

}
