package net.buildabrowser.babbrowser.render.paint;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public final class PaintUtil {
  
  private PaintUtil() {}

  public static void maybePaintFragment(
    BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection, FragmentPaintFunc func
  ) {
    if (!aabbFragmentVp(fragment, vpIntersection)) return;

    int vX = vpIntersection[0], vY = vpIntersection[1], vW = vpIntersection[2], vH = vpIntersection[3];
    // Though the AABB check is done by border, fragments typically only care about vpIntersection for culling
    // children in their content section
    vpIntersection[0] -= fragment.posX(Measurement.CONTENT);
    vpIntersection[1] -= fragment.posY(Measurement.CONTENT);
    // TODO: Also intersect width/height
    func.paint(fragment, canvas, vpIntersection);
    
    vpIntersection[0] = vX; vpIntersection[1] = vY; vpIntersection[2] = vW; vpIntersection[3] = vH;
  }

  public static <T extends LayoutFragment> void maybePaintGenericFragment(
    T fragment, PaintCanvas canvas, int[] vpIntersection, GenericFragmentPaintFunc<T> func
  ) {
    int vX = vpIntersection[0], vY = vpIntersection[1], vW = vpIntersection[2], vH = vpIntersection[3];
    vpIntersection[0] -= fragment.posX(Measurement.CONTENT);
    vpIntersection[1] -= fragment.posY(Measurement.CONTENT);
    func.paint(fragment, canvas, vpIntersection);
    
    vpIntersection[0] = vX; vpIntersection[1] = vY; vpIntersection[2] = vW; vpIntersection[3] = vH;
  }

  private static boolean aabbFragmentVp(BoxFragment fragment, int[] vpIntersection) {
    return
      fragment.posX(Measurement.BORDER) < vpIntersection[0] + vpIntersection[2]
      && vpIntersection[0] < fragment.posX(Measurement.BORDER) + fragment.inkWidth(Measurement.BORDER)
      && fragment.posY(Measurement.BORDER) < vpIntersection[1] + vpIntersection[3]
      && vpIntersection[1] < fragment.posY(Measurement.BORDER) + fragment.inkHeight(Measurement.BORDER);
  }

  public static interface FragmentPaintFunc {
  
    void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection);

  }

  public static interface GenericFragmentPaintFunc<T extends LayoutFragment> {
  
    void paint(T fragment, PaintCanvas canvas, int[] vpIntersection);

  }

}
