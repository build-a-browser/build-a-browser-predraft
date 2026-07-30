package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintManagedBackground;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowInlineBoxPainterUtils.paintInlineManagedBoxFragment;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowInlineBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class FlowInlineBoxPainter implements BoxPainter<FlowInlineBoxFragment> {
  
  @Override
    public void paint(FlowInlineBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintInlineManagedBoxFragment(fragment, canvas, vpIntersection);
    }

    @Override
    public void paintBackground(FlowInlineBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintManagedBackground(canvas, fragment, vpIntersection);
    }

}
