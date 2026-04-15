package net.buildabrowser.babbrowser.render.paint;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;

public class UnreachableBoxPainter implements BoxPainter {
  
  private Exception reachException;

  public UnreachableBoxPainter() {
    this.reachException = new Exception("Source Stack");
  }

  public UnreachableBoxPainter(Element relatedElement) {
    this.reachException = new Exception("Source Stack " + relatedElement);
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    throw new IllegalStateException("This method should be unreachable!", reachException);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    throw new IllegalStateException("This method should be unreachable!", reachException);
  }
  
}
