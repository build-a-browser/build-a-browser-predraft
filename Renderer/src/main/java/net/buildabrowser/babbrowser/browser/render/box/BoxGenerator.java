package net.buildabrowser.babbrowser.browser.render.box;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.imp.BoxGeneratorImp;
import net.buildabrowser.babbrowser.dom.Node;

public interface BoxGenerator {
  
  List<Box> box(Node node);

  static BoxGenerator create() {
    return new BoxGeneratorImp();
  };

}