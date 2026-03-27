package net.buildabrowser.babbrowser.render.content.flow;

import static net.buildabrowser.babbrowser.render.layout.StackingContext.startsStackingContext;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;;

public final class FlowRootContentPainter {

  public static BoxPainter FLOW_BLOCK_PAINTER = new FlowBlockPainter();
  public static BoxPainter FLOW_INLINE_PAINTER = new FlowInlinePainter();
 
  private FlowRootContentPainter() {}

  public static class FlowRootBoxPainter implements BoxPainter {
  
    private final FlowRootContent rootContent;

    public FlowRootBoxPainter(FlowRootContent rootContent) {
      this.rootContent = rootContent;
    }

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      ManagedBoxFragment baseFragment = fragment instanceof ManagedBoxFragment managedFragment ?
        managedFragment :
        rootContent.rootFragment();
      canvas.pushPaint();
      paintBlockLevelBackgrounds(canvas, baseFragment, baseFragment);
      paintFloats(canvas, rootContent.floatTracker(), baseFragment);
      paintFragment(canvas, baseFragment, baseFragment);
      canvas.popPaint();
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      // TODO: This triggers too early for <body>
      paintBackgroundAndAdvance(canvas, fragment);
    }

    public static void paintFloats(PaintCanvas canvas, FloatTracker floatTracker, BoxFragment refFragment) {
      for (LayoutFragment childFragment: floatTracker.allFloats()) {
        if (startsStackingContext(childFragment, refFragment)) continue;

        canvas.pushPaint();
        canvas.alterPaint(paint -> paint.incOffset(
          childFragment.borderX(),
          childFragment.borderY()));
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
        paint.incOffset(childFragment.contentX(), childFragment.contentY());
        if (parentBox.layoutContext() != null) {
          // TODO: Remove this if once I move some stuff to fixupChildren
          paint.setFont(parentBox.layoutContext().font());
        }
        paint.setColor(parentBox.activeStyles().textColor());
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
        paint.incOffset(childFragment.borderX(), childFragment.borderY());
        paint.setFont(parentBox.layoutContext().font());
        paint.setColor(parentBox.activeStyles().textColor());
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
      canvas.alterPaint(paint -> paint.incOffset(childFragment.borderX(), childFragment.borderY()));
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
      canvas.alterPaint(paint -> paint.incOffset(childFragment.borderX(), childFragment.borderY()));
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
      fragment.contentX() - fragment.borderX(),
      fragment.contentY() - fragment.borderY()));
  }

}
