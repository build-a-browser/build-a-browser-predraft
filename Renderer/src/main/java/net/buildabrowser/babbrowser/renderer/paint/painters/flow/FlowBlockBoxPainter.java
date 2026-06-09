package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintBlockBackground;
import static net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBoxPainterUtils.paintManagedBoxFragment;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowBlockBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class FlowBlockBoxPainter implements BoxPainter<FlowBlockBoxFragment> {
  
    @Override
    public void paint(FlowBlockBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintManagedBoxFragment(fragment, canvas, vpIntersection, fragment);
    }

    @Override
    public void paintBackground(FlowBlockBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
      paintBlockBackground(fragment, canvas, vpIntersection, fragment);
    }

}
