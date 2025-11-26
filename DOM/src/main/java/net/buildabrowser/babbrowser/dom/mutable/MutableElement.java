package net.buildabrowser.babbrowser.dom.mutable;

import java.util.Map;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableElementImp;

public interface MutableElement extends Element, MutableNode {
 
  Element immutable();

  static MutableElement create(String name, Map<String,String> attributes) {
    return new MutableElementImp(name, attributes);
  }

}
