package net.buildabrowser.babbrowser.renderer.content.flow;

import static net.buildabrowser.babbrowser.renderer.layout.StackingContext.startsStackingContext;

import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;;

public final class FlowRootContentPainter {

  public static BoxPainter FLOW_ROOT_PAINTER = new FlowRootBoxPainter();
  public static BoxPainter FLOW_BLOCK_PAINTER = new FlowBlockPainter();
  public static BoxPainter FLOW_INLINE_PAINTER = new FlowInlinePainter();
 
  private FlowRootContentPainter() {}

  public static class FlowRootBoxPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      FlowRootBoxFragment wrapperFragment = (FlowRootBoxFragment) fragment;
      ManagedBoxFragment baseFragment = wrapperFragment.rootFragment();
      paintBlockLevelBackgrounds(baseFragment, canvas, vpIntersection, baseFragment);
      paintFloats(wrapperFragment.floats(), canvas, vpIntersection, baseFragment);
      paintManagedBoxFragment(baseFragment, canvas, vpIntersection, baseFragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      // TODO: This triggers too early for <body>
      paintManagedBackground(canvas, fragment);
    }

    public static void paintFloats(List<BoxFragment> floats, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment) {
      for (BoxFragment childFragment: floats) {
        if (startsStackingContext(childFragment, refFragment)) continue;

        canvas.withTransform(
          t -> t.translate(
            childFragment.posX(Measurement.BORDER),
            childFragment.posY(Measurement.BORDER)),
          c -> {
            paintUnmanagedBackgroundThen(
              c, (BoxFragment) childFragment, vpIntersection,
              c2 -> paintFragment(childFragment, c2, vpIntersection, refFragment));
          });
      }
    }

  }

  private static class FlowBlockPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintManagedBoxFragment((ManagedBoxFragment) fragment, canvas, vpIntersection, fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintBlockBackground(fragment, canvas, vpIntersection, fragment);
    }

  }

  private static class FlowInlinePainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintInlineManagedBoxFragment((ManagedBoxFragment) fragment, canvas, vpIntersection, (BoxFragment) fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintManagedBackground(canvas, fragment);
    }

  }

  public static void paintFragment(
    LayoutFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
  ) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> PaintUtil.maybePaintFragment(boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintManagedBoxFragment(boxFragment, c, vpi, refFragment));
      case UnmanagedBoxFragment boxFragment -> PaintUtil.maybePaintFragment(
        boxFragment, canvas, vpIntersection, boxFragment.painter()::paint);
      // TODO: Make sure it was shifted by line box content
      case LineBoxFragment lineboxFragment -> PaintUtil.maybePaintGenericFragment(
        lineboxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintLineBoxFragment(f, c, vpi, refFragment));
      case PosRefBoxFragment _1 -> {}
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!" + fragment);
    }
  }

  private static void paintManagedBoxFragment(
    ManagedBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
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
          paint.setColor(ActiveStylesUtil.textColor(parentBox.activeStyles()));
        },
        t -> t.translate(childFragment.posX(Measurement.CONTENT), childFragment.posY(Measurement.CONTENT)),
        c -> paintFragment(childFragment, c, vpIntersection, refFragment));
    }
  }

  // TODO: Unify this with above? Inline is offseting by border (then child adjusts), block-level by content
  private static void paintInlineFragment(
    LayoutFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
  ) {
    switch (fragment) {
      case PosRefBoxFragment _1 -> {}
      case ManagedBoxFragment boxFragment -> PaintUtil.maybePaintFragment(boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> paintInlineManagedBoxFragment(boxFragment, c, vpi, refFragment));
      case UnmanagedBoxFragment boxFragment -> PaintUtil.maybePaintFragment(
        boxFragment, canvas, vpIntersection,
        (f, c, vpi) -> FlowRootContentPainter.paintInlineUnmanagedBoxFragment(boxFragment, c, vpi));
      case TextFragment textFragment -> paintTextFragment(canvas, textFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type! " + fragment.getClass());
    }
  }

  private static void paintInlineManagedBoxFragment(
    ManagedBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
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
          p.setColor(ActiveStylesUtil.textColor(parentBox.activeStyles()));
        },
        t -> t.translate(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)),
        c -> paintInlineFragment(childFragment, c, vpIntersection, refFragment));
    }
  }

  private static void paintInlineUnmanagedBoxFragment(
    UnmanagedBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection
  ) {
    paintUnmanagedBackgroundThen(
      canvas, fragment, vpIntersection,
      c -> fragment.painter().paint(fragment, c, vpIntersection));
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(
    LineBoxFragment lineboxFragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
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

  private static void paintBlockBackground(
    BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
  ) {
    if (!(fragment instanceof ManagedBoxFragment managedBoxFragment)) {
      paintUnmanagedBackground(canvas, fragment, vpIntersection);
      return;
    }
    
    paintManagedBackgroundThen(
      canvas, fragment,
      c -> paintBlockLevelBackgrounds(managedBoxFragment, c, vpIntersection, refFragment));
  }

  private static void paintBlockLevelBackgrounds(
    ManagedBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, BoxFragment refFragment
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

  private static void paintManagedBackground(PaintCanvas canvas, LayoutFragment fragment) {
    if (fragment instanceof BoxFragment boxFragment) {
      ElementBackgroundPainter.paintBackground(canvas, boxFragment);
    }
  }

  private static void paintManagedBackgroundThen(
    PaintCanvas canvas, LayoutFragment fragment,
    Consumer<PaintCanvas> paintFunc
  ) {
    paintManagedBackground(canvas, fragment);

    canvas.withTransform(
      p -> p.translate(
        fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)),
      paintFunc);
  }

  private static void paintUnmanagedBackground(
    PaintCanvas canvas, BoxFragment fragment, VpIntersection vpIntersection
  ) {
    PaintUtil.maybePaintFragment(
      fragment, canvas, vpIntersection,
      fragment.painter()::paintBackground,
      Measurement.BORDER);
  }

  private static void paintUnmanagedBackgroundThen(
    PaintCanvas canvas, BoxFragment fragment, VpIntersection vpIntersection,
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
