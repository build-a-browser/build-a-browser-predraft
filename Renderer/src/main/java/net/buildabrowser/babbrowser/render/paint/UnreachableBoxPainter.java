package net.buildabrowser.babbrowser.render.paint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;

public class UnreachableBoxPainter implements BoxPainter {
  
  private static Logger LOGGER = LoggerFactory.getLogger(UnreachableBoxPainter.class);

  private Exception reachException;

  public UnreachableBoxPainter() {
    this.reachException = new Exception("Source Stack");
  }

  public UnreachableBoxPainter(Element relatedElement) {
    this.reachException = new Exception("Source Stack " + relatedElement);
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    assert false : "This method should be unreachable!";
    if (this.reachException != null) {
      LOGGER.error("This method should be unreachable!", new Exception());
      LOGGER.error("Unreachable Painter Source", reachException);
      this.reachException = null;
    }
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    paint(fragment, canvas);
  }
  
}
