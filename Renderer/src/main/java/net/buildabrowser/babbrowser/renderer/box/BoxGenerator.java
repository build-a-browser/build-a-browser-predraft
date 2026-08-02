package net.buildabrowser.babbrowser.renderer.box;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.imp.BoxGeneratorImp;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public interface BoxGenerator {
  
  List<Box> box(Box parentBox, Node node);

  void fixup(Box box);

  static BoxGenerator create(
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    return new BoxGeneratorImp(renderContexts);
  }

}