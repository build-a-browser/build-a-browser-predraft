package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public final class PaintUtil {
  
  private PaintUtil() {}

  public static void maybePaintFragment(
    BoxFragment<?> fragment, PaintCanvas canvas, VpIntersection vpIntersection, FragmentPaintFunc func
  ) {
    maybePaintFragment(fragment, canvas, vpIntersection, func, Measurement.CONTENT);
  }

  public static void maybePaintFragment(
    BoxFragment<?> fragment, PaintCanvas canvas, VpIntersection vpIntersection,
    FragmentPaintFunc func, Measurement measurement
  ) {
    if (fragment instanceof PosRefBoxFragment) return;
    if (!aabbFragmentVp(fragment, vpIntersection, measurement)) return;

    vpIntersection.enterEl(
      fragment.posX(measurement),
      fragment.posY(measurement),
      vpi -> func.paint(fragment, canvas, vpi));
  }

  // TODO: Why were maybePaintFragment and maybePaintGenericFragment distinct again?
  // But this variant does not cull
  public static <T extends LayoutFragment> void maybePaintGenericFragment(
    T fragment, PaintCanvas canvas, VpIntersection vpIntersection, GenericFragmentPaintFunc<T> func
  ) {
    Measurement measurement = Measurement.CONTENT;
    vpIntersection.enterEl(
      fragment.posX(measurement),
      fragment.posY(measurement),
      vpi -> func.paint(fragment, canvas, vpi));
  }

  // TODO: Use width instead of inkWidth for clipped elements
  private static boolean aabbFragmentVp(
    BoxFragment<?> fragment, VpIntersection vpIntersection, Measurement measurement
  ) {
    float elPosX = vpIntersection.elVpX() + fragment.posX(measurement);
    float elPosY = vpIntersection.elVpY() + fragment.posY(measurement);
    return
      elPosX < vpIntersection.bufferX() + vpIntersection.bufferWidth()
      && vpIntersection.bufferX() < elPosX + fragment.inkWidth(measurement)
      && elPosY < vpIntersection.bufferY() + vpIntersection.bufferHeight()
      && vpIntersection.bufferY() < elPosY + fragment.inkHeight(measurement);
  }

  public static interface FragmentPaintFunc {
  
    void paint(BoxFragment<?> fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  }

  public static interface GenericFragmentPaintFunc<T extends LayoutFragment> {
  
    void paint(T fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  }

}
