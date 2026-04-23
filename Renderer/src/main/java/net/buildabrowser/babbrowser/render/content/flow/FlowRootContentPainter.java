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
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;;

public final class FlowRootContentPainter {

  public static BoxPainter FLOW_ROOT_PAINTER = new FlowRootBoxPainter();
  public static BoxPainter FLOW_BLOCK_PAINTER = new FlowBlockPainter();
  public static BoxPainter FLOW_INLINE_PAINTER = new FlowInlinePainter();
 
  private FlowRootContentPainter() {}

  public static class FlowRootBoxPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      FlowRootBoxFragment wrapperFragment = (FlowRootBoxFragment) fragment;
      ManagedBoxFragment baseFragment = wrapperFragment.rootFragment();
      canvas.pushPaint();
      paintBlockLevelBackgrounds(canvas, baseFragment, baseFragment);
      paintFloats(canvas, wrapperFragment.floats(), baseFragment);
      paintFragment(canvas, baseFragment, baseFragment);
      canvas.popPaint();
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      // TODO: This triggers too early for <body>
      paintBackgroundAndAdvance(canvas, fragment);
    }

    public static void paintFloats(PaintCanvas canvas, List<BoxFragment> floats, BoxFragment refFragment) {
      for (BoxFragment childFragment: floats) {
        if (startsStackingContext(childFragment, refFragment)) continue;

        canvas.pushPaint();
        canvas.alterPaint(paint -> paint.incOffset(
          childFragment.posX(Measurement.BORDER),
          childFragment.posY(Measurement.BORDER)));
        paintBackgroundAndAdvance(canvas, (BoxFragment) childFragment);
        paintFragment(canvas, childFragment, refFragment);
        canvas.popPaint();
      }
    }

  }

  private static class FlowBlockPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      paintManagedBoxFragment(canvas, (ManagedBoxFragment) fragment, fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      paintBackgroundAndAdvance(canvas, fragment);
      if (fragment instanceof ManagedBoxFragment managedBoxFragment) {
        paintBlockLevelBackgrounds(canvas, managedBoxFragment, fragment);
      }
    }

  }

  private static class FlowInlinePainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      paintInlineManagedBoxFragment(canvas, (ManagedBoxFragment) fragment, (BoxFragment) fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {}

  }

  public static void paintFragment(
    PaintCanvas canvas, LayoutFragment fragment, BoxFragment refFragment
  ) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> paintManagedBoxFragment(canvas, boxFragment, refFragment);
      case UnmanagedBoxFragment boxFragment -> boxFragment.painter().paint(boxFragment, canvas);
      case LineBoxFragment lineboxFragment -> paintLineBoxFragment(canvas, lineboxFragment, refFragment);
      case PosRefBoxFragment _ -> {}
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!" + fragment);
    }
  }

  private static void paintManagedBoxFragment(
    PaintCanvas canvas, ManagedBoxFragment fragment, BoxFragment refFragment
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
      paintFragment(canvas, childFragment, refFragment);
      canvas.popPaint();
    }
  }

  // TODO: Unify this with above? Inline is offseting by border (then child adjusts), block-level by content
  private static void paintInlineFragment(
    PaintCanvas canvas, LayoutFragment fragment, BoxFragment refFragment
  ) {
    switch (fragment) {
      case PosRefBoxFragment _ -> {}
      case ManagedBoxFragment boxFragment -> paintInlineManagedBoxFragment(canvas, boxFragment, refFragment);
      case UnmanagedBoxFragment boxFragment -> paintInlineUnmanagedBoxFragment(canvas, boxFragment);
      case TextFragment textFragment -> paintTextFragment(canvas, textFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type! " + fragment.getClass());
    }
  }

  private static void paintInlineManagedBoxFragment(
    PaintCanvas canvas, ManagedBoxFragment fragment, BoxFragment refFragment
  ) {
    paintBackgroundAndAdvance(canvas, fragment);

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
      paintInlineFragment(canvas, childFragment, refFragment);
      canvas.popPaint();
    }
  }

  private static void paintInlineUnmanagedBoxFragment(PaintCanvas canvas, UnmanagedBoxFragment fragment) {
    paintBackgroundAndAdvance(canvas, fragment);
    fragment.painter().paint(fragment, canvas);
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(
    PaintCanvas canvas, LineBoxFragment lineboxFragment, BoxFragment refFragment
  ) {
    LayoutFragment curNode = lineboxFragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)));
      paintInlineFragment(canvas, childFragment, refFragment);
      canvas.popPaint();
    }
  }

  private static void paintBlockLevelBackgrounds(PaintCanvas canvas, ManagedBoxFragment fragment, BoxFragment refFragment) {
    LayoutFragment curNode = fragment.fragments();
    while (curNode != null) {
      LayoutFragment childFragment = curNode;
      curNode = curNode.next();
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(Measurement.BORDER), childFragment.posY(Measurement.BORDER)));
      switch (childFragment) {
        case ManagedBoxFragment managedFragment:
          paintBackgroundAndAdvance(canvas, managedFragment);
          paintBlockLevelBackgrounds(canvas, managedFragment, refFragment);
          break;
        case UnmanagedBoxFragment unmanagedFragment:
          paintBackgroundAndAdvance(canvas, unmanagedFragment);
          break;
        default:
          break;
      }
      canvas.popPaint();
    }
  }

  private static void paintBackgroundAndAdvance(PaintCanvas canvas, BoxFragment fragment) {
    ElementBackgroundPainter.paintBackground(canvas, fragment);

    canvas.alterPaint(paint -> paint.incOffset(
      fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
      fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)));
  }

}
