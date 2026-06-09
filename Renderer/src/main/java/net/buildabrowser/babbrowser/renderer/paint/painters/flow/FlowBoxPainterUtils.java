package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import static net.buildabrowser.babbrowser.renderer.layout.StackingContext.startsStackingContext;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;;

public final class FlowBoxPainterUtils {
 
  private FlowBoxPainterUtils() {}

  public static void paintFragment(
    LayoutFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    switch (fragment) {
      case ManagedBoxFragment<?> boxFragment -> PaintUtil.maybePaintFragment(boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintManagedBoxFragment(boxFragment, c, vpi, refFragment));
      case UnmanagedBoxFragment<?> boxFragment -> PaintUtil.maybePaintFragment(
        boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> f.withPainterV((p, f2) -> p.paint(f2, c, vpi)));
      // TODO: Make sure it was shifted by line box content
      case LineBoxFragment lineboxFragment -> PaintUtil.maybePaintGenericFragment(
        lineboxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintLineBoxFragment(f, c, vpi, refFragment));
      case PosRefBoxFragment _1 -> {}
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!" + fragment);
    }
  }

  static void paintManagedBoxFragment(
    ManagedBoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    ElementBox parentBox = fragment.box();
    
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.withPaintAndTransform(
        paint -> {
          if (parentBox.layoutContext() != null) {
            // TODO: Remove this if once I move some stuff to fixupChildren
            paint.setFont(parentBox.layoutContext().font());
          }
          paint.setColor(PropertiesUtil.textColor(parentBox.properties()));
        },
        t -> t.translate(childFragment.posX(Measurement.CONTENT), childFragment.posY(Measurement.CONTENT)),
        c -> paintFragment(childFragment, c, vpIntersection, refFragment));
    }
  }

  // TODO: Unify this with above? Inline is offseting by border (then child adjusts), block-level by content
  private static void paintInlineFragment(
    LayoutFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    switch (fragment) {
      case PosRefBoxFragment _1 -> {}
      case ManagedBoxFragment<?> boxFragment -> PaintUtil.maybePaintFragment(boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintInlineManagedBoxFragment(boxFragment, c, vpi, refFragment));
      case UnmanagedBoxFragment<?> boxFragment -> PaintUtil.maybePaintFragment(
        boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintInlineUnmanagedBoxFragment(boxFragment, c, vpi));
      case TextFragment textFragment -> paintTextFragment(canvas, textFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type! " + fragment.getClass());
    }
  }

  static void paintInlineManagedBoxFragment(
    ManagedBoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    ElementBox parentBox = fragment.box();
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.withPaintAndTransform(
        p -> {
          p.setFont(parentBox.layoutContext().font());
          p.setColor(PropertiesUtil.textColor(parentBox.properties()));
        },
        t -> t.translate(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)),
        c -> paintInlineFragment(childFragment, c, vpIntersection, refFragment));
    }
  }

  private static void paintInlineUnmanagedBoxFragment(
    UnmanagedBoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    paintUnmanagedBackgroundThen(
      canvas, fragment, vpIntersection,
      c -> fragment.withPainterV((p, f) -> p.paint(f, c, vpIntersection)));
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(
    LineBoxFragment lineboxFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    LayoutFragment curNode = lineboxFragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.withTransform(
        t -> t.translate(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)),
        c -> paintInlineFragment(childFragment, c, vpIntersection, refFragment));
    }
  }

  static void paintBlockBackground(
    BoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    if (!(fragment instanceof ManagedBoxFragment managedBoxFragment)) {
      paintUnmanagedBackground(canvas, fragment, vpIntersection);
      return;
    }
    
    paintManagedBackgroundThen(
      canvas, fragment, vpIntersection,
      c -> paintBlockLevelBackgrounds(managedBoxFragment, c, vpIntersection, refFragment));
  }

  static void paintBlockLevelBackgrounds(
    ManagedBoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      if (childFragment instanceof BoxFragment childBoxFragment) {
        canvas.withTransform(
          t -> t.translate(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)),
          c -> PaintUtil.maybePaintFragment(childBoxFragment, c, vpIntersection,
            (f, c2, vpi) -> paintBlockBackground(f, c2, vpi, refFragment)));
      }
    }
  }

  static void paintManagedBackground(
    PaintCanvas canvas,
    LayoutFragment fragment,
    VpIntersection vpIntersection
  ) {
    if (fragment instanceof BoxFragment boxFragment) {
      ElementBackgroundPainter.paintBackground(canvas, boxFragment, vpIntersection);
    }
  }

  private static void paintManagedBackgroundThen(
    PaintCanvas canvas,
    LayoutFragment fragment,
    VpIntersection vpIntersection,
    Consumer<PaintCanvas> paintFunc
  ) {
    paintManagedBackground(canvas, fragment, vpIntersection);

    canvas.withTransform(
      p -> p.translate(
        fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)),
      paintFunc);
  }

  private static void paintUnmanagedBackground(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection
  ) {
    PaintUtil.maybePaintFragment(
      fragment, canvas, vpIntersection,
      (f, c, vpi) -> fragment.withPainterV((p, f2) -> p.paintBackground(f2, c, vpi)),
      Measurement.BORDER);
  }

  static void paintUnmanagedBackgroundThen(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection,
    Consumer<PaintCanvas> paintFunc
  ) {
    paintUnmanagedBackground(canvas, fragment, vpIntersection);
    canvas.withTransform(
      t -> t.translate(
        fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)),
      paintFunc);
  }

}
