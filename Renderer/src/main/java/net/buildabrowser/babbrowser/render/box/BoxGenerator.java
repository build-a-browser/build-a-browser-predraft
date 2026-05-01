package net.buildabrowser.babbrowser.render.box;

import java.util.List;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.render.box.imp.BoxGeneratorImp;

public interface BoxGenerator {
  
  List<Box> box(Box parentBox, Node node);

  void fixup(Box box);

  static BoxGenerator create() {
    return new BoxGeneratorImp();
  }

}