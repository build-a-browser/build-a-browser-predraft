package net.buildabrowser.babbrowser.css.engine.matcher;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.dom.Element;

public interface ElementSetListener extends IntrusiveList<ElementSetListener> {
  
  void onResize(int size);

  void onElementAdded(Element element);

  void onElementRemoved(Element element);

}
