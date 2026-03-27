package net.buildabrowser.babbrowser.render.box;

public interface DocumentBox extends Box {
  
  ElementBox htmlBox();

  void setChild(ElementBox child);

}
