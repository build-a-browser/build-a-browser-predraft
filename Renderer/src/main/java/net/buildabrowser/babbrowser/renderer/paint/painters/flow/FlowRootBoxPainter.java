package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import static net.buildabrowser.babbrowser.renderer.layout.StackingContext.startsStackingContext;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintBlockLevelBackgrounds;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintFragment;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintManagedBackground;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintManagedBoxFragment;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintUnmanagedBackgroundThen;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class FlowRootBoxPainter implements BoxPainter<FlowRootBoxFragment> {
  
  @Override
  public void paint(FlowRootBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    ManagedBoxFragment<?> baseFragment = fragment.rootFragment();
    paintBlockLevelBackgrounds(baseFragment, canvas, vpIntersection, baseFragment);
    paintFloats(fragment.floats(), canvas, vpIntersection, baseFragment);
    paintManagedBoxFragment(baseFragment, canvas, vpIntersection, baseFragment);
  }

  @Override
  public void paintBackground(FlowRootBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    // TODO: This triggers too early for <body>
    paintManagedBackground(canvas, fragment, vpIntersection);
  }

  public static void paintFloats(
    List<BoxFragment<?>> floats,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    BoxFragment<?> refFragment
  ) {
    for (BoxFragment<?> childFragment: floats) {
      if (startsStackingContext(childFragment, refFragment)) continue;

      canvas.withTransform(
        t -> t.translate(
          childFragment.posX(Measurement.BORDER),
          childFragment.posY(Measurement.BORDER)),
        c -> {
          paintUnmanagedBackgroundThen(
            c, (BoxFragment<?>) childFragment, vpIntersection,
            c2 -> paintFragment(childFragment, c2, vpIntersection, refFragment));
        });
    }
  }

}
