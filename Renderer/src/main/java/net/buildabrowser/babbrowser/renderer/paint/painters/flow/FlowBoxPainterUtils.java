package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import static net.buildabrowser.babbrowser.renderer.layout.StackingContext.startsStackingContext;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil.FragmentPaintFunc;
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
      case ManagedBoxFragment<?> boxFragment -> paintManagedBoxFragment(
        boxFragment, canvas, vpIntersection, refFragment);
      case UnmanagedBoxFragment<?> boxFragment -> boxFragment.withPainterV(
        (p, f) -> p.paint(f, canvas, vpIntersection));
      // TODO: Make sure it was shifted by line box content
      case LineBoxFragment lineboxFragment -> PaintUtil.maybePaintFragment(
        lineboxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintLineBoxFragment(f, c, vpi, refFragment));
      case PosRefBoxFragment _1 -> {}
      case FloatRefFragment _1 -> {}
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

      withFragmentContent(
        canvas, vpIntersection, refFragment, parentBox, childFragment,
        (f, c, vpi) -> paintFragment(f, c, vpi, refFragment));
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
      case FloatRefFragment _1 -> {}
      case ManagedBoxFragment<?> boxFragment -> paintInlineManagedBoxFragment(
        boxFragment, canvas, vpIntersection, refFragment);
      case UnmanagedBoxFragment<?> boxFragment -> paintInlineUnmanagedBoxFragment(
        boxFragment, canvas, vpIntersection);
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

      withFragmentContent(
        canvas, vpIntersection, refFragment, parentBox, childFragment,
        (f, c, vpi) -> paintInlineFragment(f, c, vpi, refFragment));
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

      withFragmentContent(
        canvas, vpIntersection, refFragment, childFragment,
        (f, c, vpi) -> paintInlineFragment(f, c, vpi, refFragment));
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
    fragment.withPainterV((p, f) -> p.paintBackground(f, canvas, vpIntersection));
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

  private static <T extends LayoutFragment> void withFragmentContent(
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment,
    T childFragment,
    FragmentPaintFunc<T> func
  ) {
    canvas.withTransform(
      t -> t.translate(childFragment.posX(Measurement.CONTENT), childFragment.posY(Measurement.CONTENT)),
      c -> PaintUtil.maybePaintFragment(childFragment, c, vpIntersection, func));
  }

  private static <T extends LayoutFragment> void withFragmentContent(
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment,
    ElementBox parentBox,
    T childFragment,
    FragmentPaintFunc<T> func
  ) {
    canvas.withPaintAndTransform(
      paint -> {
        paint.setFont(parentBox.layoutContext().font());
        paint.setColor(PropertiesUtil.textColor(parentBox.properties()));
      },
      t -> t.translate(childFragment.posX(Measurement.CONTENT), childFragment.posY(Measurement.CONTENT)),
      c -> PaintUtil.maybePaintFragment(childFragment, c, vpIntersection, func));
  }

}
