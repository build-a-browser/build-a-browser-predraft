package net.buildabrowser.babbrowser.renderer.paint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;

public class UnreachableBoxPainter implements BoxPainter {
  
  private static final BoxPainter UNREACHABLE_BOX_PAINTER_NO_TRACE = new UnreachableBoxPainterNoTrace();

  private static Logger LOGGER = LoggerFactory.getLogger(UnreachableBoxPainter.class);
  private static boolean INCLUDE_TRACE = UnreachableBoxPainter.class.desiredAssertionStatus();

  private final Exception reachException;

  private UnreachableBoxPainter() {
    this.reachException = new Exception("Source Stack");
  }

  private UnreachableBoxPainter(Element relatedElement) {
    String elemName = relatedElement == null ? "<anon>" : relatedElement.name();
    this.reachException = new Exception("Source Stack (for element named " + elemName +")");
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    LOGGER.error("Unreachable Painter Source", reachException);

    assert false : "This method should be unreachable!";
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    paint(fragment, canvas, vpIntersection);
  }

  public static BoxPainter create() {
    if (INCLUDE_TRACE) {
      return new UnreachableBoxPainter();
    } else {
      return UNREACHABLE_BOX_PAINTER_NO_TRACE;
    }
  }

  public static BoxPainter create(Element relatedElement) {
    if (INCLUDE_TRACE) {
      return new UnreachableBoxPainter(relatedElement);
    } else {
      return UNREACHABLE_BOX_PAINTER_NO_TRACE;
    }
  }

  private static class UnreachableBoxPainterNoTrace implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      LOGGER.warn("Reached UnreachableBoxPainterNoTrace#paint - this should not happen!");
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      LOGGER.warn("Reached UnreachableBoxPainterNoTrace#paintBackground - this should not happen!");
    }

  }
  
}
