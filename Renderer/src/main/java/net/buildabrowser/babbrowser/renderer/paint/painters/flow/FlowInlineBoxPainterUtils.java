package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import static net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext.startsStackingContext;

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
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;

public final class FlowInlineBoxPainterUtils {
  
  private FlowInlineBoxPainterUtils() {}

  // Block painting starts at border-edge

  public static void paintLineBoxFragment(
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

      paintInlineFragment(null, childFragment, canvas, vpIntersection, refFragment);
    }
  }
  
  private static void paintInlineFragment(
    ElementBox parentBox,
    LayoutFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    PaintUtil.maybePaintBgFragment(
      fragment, canvas, vpIntersection,
      (f, c, vpi) -> c.withTransform(
        t -> t.translate(
          f.posX(Measurement.BORDER),
          f.posY(Measurement.BORDER)),
        c2 -> paintInlineFragmentBackground(f, c2, vpi)));
    FlowBoxPainterUtils.withFragmentContent(
      canvas, vpIntersection, refFragment,
      parentBox, fragment,
      (f, c2, vpi) -> paintInlineFragmentForeground(
        f, c2, vpi, refFragment));
  }

  private static void paintInlineFragmentBackground(
    LayoutFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    switch (fragment) {
      case PosRefBoxFragment _1 -> {}
      case FloatRefFragment _1 -> {}
      case ManagedBoxFragment<?> boxFragment ->
        ElementBackgroundPainter.paintBackground(canvas, boxFragment, vpIntersection);
      case UnmanagedBoxFragment<?> boxFragment ->
        boxFragment.withPainterV((p, f) -> p.paintBackground(f, canvas, vpIntersection));
      case TextFragment textFragment -> {}
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type! " + fragment.getClass());
    }
  }

  private static void paintInlineFragmentForeground(
    LayoutFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    switch (fragment) {
      case PosRefBoxFragment _1 -> {}
      case FloatRefFragment _1 -> {}
      case ManagedBoxFragment<?> boxFragment -> paintInlineManagedBoxFragment(
        boxFragment, canvas, vpIntersection);
      case UnmanagedBoxFragment<?> boxFragment -> paintInlineUnmanagedBoxFragment(
        boxFragment, canvas, vpIntersection);
      case TextFragment textFragment -> FlowTextPainter.paintTextFragment(
        canvas, refFragment, textFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type! " + fragment.getClass());
    }
  }

  static void paintInlineManagedBoxFragment(
    ManagedBoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBackgroundPainter.paintBackground(canvas, fragment, vpIntersection);

    ElementBox parentBox = fragment.box();
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, fragment)) continue;

      paintInlineFragment(parentBox, childFragment, canvas, vpIntersection, fragment);
    }
  }

  private static void paintInlineUnmanagedBoxFragment(
    UnmanagedBoxFragment<?> fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    fragment.withPainterV((p, f) -> p.paint(f, canvas, vpIntersection));
  }

}
