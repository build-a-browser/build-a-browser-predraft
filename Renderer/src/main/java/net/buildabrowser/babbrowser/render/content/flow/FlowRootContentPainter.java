package net.buildabrowser.babbrowser.render.content.flow;

import static net.buildabrowser.babbrowser.render.layout.StackingContext.startsStackingContext;

import java.util.List;

import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.PaintUtil;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;;

public final class FlowRootContentPainter {

  public static BoxPainter FLOW_ROOT_PAINTER = new FlowRootBoxPainter();
  public static BoxPainter FLOW_BLOCK_PAINTER = new FlowBlockPainter();
  public static BoxPainter FLOW_INLINE_PAINTER = new FlowInlinePainter();
 
  private FlowRootContentPainter() {}

  public static class FlowRootBoxPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      FlowRootBoxFragment wrapperFragment = (FlowRootBoxFragment) fragment;
      ManagedBoxFragment baseFragment = wrapperFragment.rootFragment();
      canvas.pushPaint();
      paintBlockLevelBackgrounds(baseFragment, canvas, vpIntersection, baseFragment);
      paintFloats(wrapperFragment.floats(), canvas, vpIntersection, baseFragment);
      paintManagedBoxFragment(baseFragment, canvas, vpIntersection, baseFragment);
      canvas.popPaint();
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      // TODO: This triggers too early for <body>
      paintBackgroundAndAdvance(canvas, fragment);
    }

    public static void paintFloats(List<BoxFragment> floats, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment) {
      for (BoxFragment childFragment: floats) {
        if (startsStackingContext(childFragment, refFragment)) continue;

        canvas.pushPaint();
        canvas.alterPaint(paint -> paint.incOffset(
          childFragment.posX(Measurement.BORDER),
          childFragment.posY(Measurement.BORDER)));
        paintUnmanagedBackgroundAndAdvance(canvas, (BoxFragment) childFragment, vpIntersection);
        paintFragment(childFragment, canvas, vpIntersection, refFragment);
        canvas.popPaint();
      }
    }

  }

  private static class FlowBlockPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      paintManagedBoxFragment((ManagedBoxFragment) fragment, canvas, vpIntersection, fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      paintBlockBackground(fragment, canvas, vpIntersection, fragment);
    }

  }

  private static class FlowInlinePainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      paintInlineManagedBoxFragment((ManagedBoxFragment) fragment, canvas, vpIntersection, (BoxFragment) fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
      paintBackgroundAndAdvance(canvas, fragment);
    }

  }

  public static void paintFragment(
    LayoutFragment fragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
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
      case PosRefBoxFragment _ -> {}
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!" + fragment);
    }
  }

  private static void paintManagedBoxFragment(
    ManagedBoxFragment fragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
  ) {
    ElementBox parentBox = fragment.box();
    
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> {
        paint.incOffset(childFragment.posX(Measurement.CONTENT), childFragment.posY(Measurement.CONTENT));
        if (parentBox.layoutContext() != null) {
          // TODO: Remove this if once I move some stuff to fixupChildren
          paint.setFont(parentBox.layoutContext().font());
        }
        paint.setColor(ActiveStylesUtil.textColor(parentBox.activeStyles()));
      });
      paintFragment(childFragment, canvas, vpIntersection, refFragment);
      canvas.popPaint();
    }
  }

  // TODO: Unify this with above? Inline is offseting by border (then child adjusts), block-level by content
  private static void paintInlineFragment(
    LayoutFragment fragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
  ) {
    switch (fragment) {
      case PosRefBoxFragment _ -> {}
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
    ManagedBoxFragment fragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
  ) {
    ElementBox parentBox = fragment.box();
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> {
        paint.incOffset(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER));
        paint.setFont(parentBox.layoutContext().font());
        paint.setColor(ActiveStylesUtil.textColor(parentBox.activeStyles()));
      });
      paintInlineFragment(childFragment, canvas, vpIntersection, refFragment);
      canvas.popPaint();
    }
  }

  private static void paintInlineUnmanagedBoxFragment(
    UnmanagedBoxFragment fragment, PaintCanvas canvas, int[] vpIntersection
  ) {
    paintUnmanagedBackgroundAndAdvance(canvas, fragment, vpIntersection);
    fragment.painter().paint(fragment, canvas, vpIntersection);
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(
    LineBoxFragment lineboxFragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
  ) {
    LayoutFragment curNode = lineboxFragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)));
      paintInlineFragment(childFragment, canvas, vpIntersection, refFragment);
      canvas.popPaint();
    }
  }

  private static void paintBlockBackground(
    BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
  ) {
    if (!(fragment instanceof ManagedBoxFragment managedBoxFragment)) {
      paintUnmanagedBackgroundAndAdvance(canvas, fragment, vpIntersection);
      return;
    }
    
    paintBackgroundAndAdvance(canvas, fragment);
    paintBlockLevelBackgrounds(managedBoxFragment, canvas, vpIntersection, refFragment);
  }

  private static void paintBlockLevelBackgrounds(
    ManagedBoxFragment fragment, PaintCanvas canvas, int[] vpIntersection, BoxFragment refFragment
  ) {
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      if (childFragment instanceof BoxFragment childBoxFragment) {
        canvas.pushPaint();
        canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)));
        PaintUtil.maybePaintFragment(childBoxFragment, canvas, vpIntersection,
          (f, c, vpi) -> paintBlockBackground(f, c, vpi, refFragment));
        
        canvas.popPaint();
      }
    }
  }

  private static void paintBackgroundAndAdvance(PaintCanvas canvas, LayoutFragment fragment) {
    if (fragment instanceof BoxFragment boxFragment) {
      ElementBackgroundPainter.paintBackground(canvas, boxFragment);
    }

    canvas.alterPaint(paint -> paint.incOffset(
      fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
      fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)));
  }

  private static void paintUnmanagedBackgroundAndAdvance(
    PaintCanvas canvas, BoxFragment fragment, int[] vpIntersection
  ) {
    canvas.pushPaint();
    PaintUtil.maybePaintFragment(
      fragment, canvas, vpIntersection,
      fragment.painter()::paintBackground,
      Measurement.BORDER);
    canvas.popPaint();

    canvas.alterPaint(paint -> paint.incOffset(
      fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
      fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)));
  }

}
