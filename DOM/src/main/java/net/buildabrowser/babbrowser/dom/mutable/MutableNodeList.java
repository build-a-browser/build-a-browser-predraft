package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableNodeListImp;

public interface MutableNodeList extends NodeList {
  
  MutableNode item(long index);

  static MutableNodeList create(MutableNode parentNode) {
    return new MutableNodeListImp(parentNode);
  }

}
