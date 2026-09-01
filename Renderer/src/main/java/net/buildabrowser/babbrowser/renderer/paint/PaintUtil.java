package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.cssbase.property.visibility.VisibilityValue;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;

public final class PaintUtil {
  
  private PaintUtil() {}

  public static <T extends LayoutFragment> void maybePaintFgFragment(
    T fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    FragmentPaintFunc<T> func
  ) {
    maybePaintFgFragment(fragment, canvas, vpIntersection, Measurement.PADDING, func);
  }

  public static <T extends LayoutFragment> void maybePaintFgFragment(
    T fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    Measurement measurement,
    FragmentPaintFunc<T> func
  ) {
    if (
      skipFragment(fragment, canvas, vpIntersection, measurement)
    ) return;

    CSSValue overflowX = fragment instanceof BoxFragment<?> boxFragment ?
      boxFragment.box().properties().get(CSSProperty.OVERFLOW_X) : null;
    CSSValue overflowY = fragment instanceof BoxFragment<?> boxFragment ?
      boxFragment.box().properties().get(CSSProperty.OVERFLOW_X) : null;
    if (causesClip(overflowX, overflowY)) {
      paintWithClip(
        fragment, canvas, vpIntersection, func,
        overflowX, overflowY);
    } else {
      func.paint(fragment, canvas, vpIntersection);
    }
  }

  public static <T extends LayoutFragment> void maybePaintBgFragment(
    T fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    FragmentPaintFunc<T> func
  ) {
    maybePaintBgFragment(fragment, canvas, vpIntersection, Measurement.BORDER, func);
  }

  public static <T extends LayoutFragment> void maybePaintBgFragment(
    T fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    Measurement measurement,
    FragmentPaintFunc<T> func
  ) {
    if (
      skipFragment(fragment, canvas, vpIntersection, measurement)
    ) return;

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

  private static boolean skipFragment(
    LayoutFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    Measurement measurement
  ) {
    if (fragment instanceof PosRefBoxFragment) return true;
    if (fragment instanceof FloatRefFragment) return true;
    if (
      fragment instanceof BoxFragment<?> boxFragment
      && (
        !aabbFragmentVp(boxFragment, vpIntersection, measurement)
        // TODO: Special handling for COLLAPSE.
        // TODO: Also the spec says in some cases visible children of a hidden element may be shown
        || !boxFragment.box().properties().get(CSSProperty.VISIBILITY).equals(VisibilityValue.VISIBLE)
    )) return true;
    return false;
  }
  
  private static boolean causesClip(CSSValue overflowX, CSSValue overflowY) {
    return
      overflowX != null
      && overflowY != null
      && !CompositeLayerUtil.causesScrollContent(overflowX)
      && !CompositeLayerUtil.causesScrollContent(overflowY)
      && (
        overflowX.equals(OverflowValue.CLIP)
        || overflowY.equals(OverflowValue.CLIP)
      );
  }

  private static <T extends LayoutFragment> void paintWithClip(
    T fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    FragmentPaintFunc<T> func,
    CSSValue overflowX,
    CSSValue overflowY
  ) {
    boolean clipX = overflowX.equals(OverflowValue.CLIP);
    float clipXStart = clipX ? fragment.posX(Measurement.PADDING) : Float.NaN;
    float clipXWidth = clipX ? fragment.width(Measurement.PADDING) : Float.NaN;
    
    boolean clipY = overflowY.equals(OverflowValue.CLIP);
    float clipYStart = clipY ? fragment.posY(Measurement.PADDING) : Float.NaN;
    float clipYHeight = clipY ? fragment.height(Measurement.PADDING) : Float.NaN;

    canvas.withClip(
      clipXStart, clipYStart, clipXWidth, clipYHeight,
      c2 -> func.paint(fragment, canvas, vpIntersection));
  }

  public static interface FragmentPaintFunc<T extends LayoutFragment> {
  
    void paint(T fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  }

}
