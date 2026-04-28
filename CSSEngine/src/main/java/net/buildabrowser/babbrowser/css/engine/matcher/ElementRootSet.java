package net.buildabrowser.babbrowser.css.engine.matcher;

public interface ElementRootSet extends ElementSet {
  
  ElementSet createChild();

  ElementSet createTemporaryChild();

  void addListener(ElementSetListener listener);

}
