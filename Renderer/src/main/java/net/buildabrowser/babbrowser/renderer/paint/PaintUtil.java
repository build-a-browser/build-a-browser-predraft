package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.visibility.VisibilityValue;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;

public final class PaintUtil {
  
  private PaintUtil() {}

  public static <T extends LayoutFragment> void maybePaintFragment(
    T fragment, PaintCanvas canvas, VpIntersection vpIntersection,
    FragmentPaintFunc<T> func
  ) {
    maybePaintFragment(fragment, canvas, vpIntersection, func, Measurement.CONTENT);
  }

  public static <T extends LayoutFragment> void maybePaintFragment(
    T fragment, PaintCanvas canvas, VpIntersection vpIntersection,
    FragmentPaintFunc<T> func, Measurement measurement
  ) {
    if (fragment instanceof PosRefBoxFragment) return;
    if (
      fragment instanceof BoxFragment<?> boxFragment
      && (
        !aabbFragmentVp(boxFragment, vpIntersection, measurement)
        // TODO: Special handling for COLLAPSE.
        // TODO: Also the spec says in some cases visible children of a hidden element may be shown
        || !boxFragment.box().properties().get(CSSProperty.VISIBILITY).equals(VisibilityValue.VISIBLE)
    )) return;

    func.paint(fragment, canvas, vpIntersection);
  }

  // TODO: Use width instead of inkWidth for clipped elements
  private static boolean aabbFragmentVp(
    BoxFragment<?> fragment, VpIntersection vpIntersection, Measurement measurement
  ) {
    float elPosX = fragment.layerX(measurement);
    float elPosY = fragment.layerY(measurement);
    return
      elPosX <= vpIntersection.bufferX() + vpIntersection.bufferWidth()
      && vpIntersection.bufferX() <= elPosX + fragment.inkWidth(measurement)
      && elPosY <= vpIntersection.bufferY() + vpIntersection.bufferHeight()
      && vpIntersection.bufferY() <= elPosY + fragment.inkHeight(measurement);
  }

  public static interface FragmentPaintFunc<T extends LayoutFragment> {
  
    void paint(T fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  }

}
