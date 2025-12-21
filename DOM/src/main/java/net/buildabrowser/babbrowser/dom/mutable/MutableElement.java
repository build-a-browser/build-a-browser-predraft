package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Namespace;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableElementImp;

public interface MutableElement extends Element, MutableNode {

  static MutableElement create(String name, MutableNode parentNode) {
    return new MutableElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
