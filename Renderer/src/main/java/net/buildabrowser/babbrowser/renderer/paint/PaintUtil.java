package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;

public final class PaintUtil {
  
  private PaintUtil() {}

  public static void maybePaintFragment(
    BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection, FragmentPaintFunc func
  ) {
    maybePaintFragment(fragment, canvas, vpIntersection, func, Measurement.CONTENT);
  }

  public static void maybePaintFragment(
    BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection,
    FragmentPaintFunc func, Measurement measurement
  ) {
    if (fragment instanceof PosRefBoxFragment) return;
    if (!aabbFragmentVp(fragment, vpIntersection)) return;

    vpIntersection.enterEl(
      fragment.posX(measurement),
      fragment.posY(measurement),
      fragment.width(measurement),
      fragment.height(measurement),
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
      fragment.width(measurement),
      fragment.height(measurement),
      vpi -> func.paint(fragment, canvas, vpi));
  }

  private static boolean aabbFragmentVp(BoxFragment fragment, VpIntersection vpIntersection) {
    return
      fragment.posX(Measurement.BORDER) < vpIntersection.elX() + vpIntersection.elWidth()
      && vpIntersection.elX() < fragment.posX(Measurement.BORDER) + fragment.inkWidth(Measurement.BORDER)
      && fragment.posY(Measurement.BORDER) < vpIntersection.elY() + vpIntersection.elHeight()
      && vpIntersection.elY() < fragment.posY(Measurement.BORDER) + fragment.inkHeight(Measurement.BORDER);
  }

  public static interface FragmentPaintFunc {
  
    void paint(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  }

  public static interface GenericFragmentPaintFunc<T extends LayoutFragment> {
  
    void paint(T fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  }

}
