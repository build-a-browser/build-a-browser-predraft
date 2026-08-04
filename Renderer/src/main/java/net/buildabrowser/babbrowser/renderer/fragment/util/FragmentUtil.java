package net.buildabrowser.babbrowser.renderer.fragment.util;

import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContextPosition;

public final class FragmentUtil {
  
  private FragmentUtil() {}

  public static void scrollIntoViewOnCurrentThread(
    BoxFragment<?> fragment
  ) {
    float fragmentOffsetY = fragment.layerY(Measurement.BORDER);
    StackingContextPosition scPosition = null;
    StackingContext currentContext = fragment.box().stackingContext();
    
    while (
      currentContext != null
    ) {
      StackingContextPosition newPosition = currentContext.position();

      ScrollBoxFragment scrollBox = currentContext.relatedScrollBox();
      if (scrollBox != null) {
        float offsetY = fragmentOffsetY;
        if (scPosition != null) {
          offsetY += (scPosition.docY() - newPosition.docY());
          scPosition = newPosition;
        }

        if (Float.isNaN(offsetY)) return;
        scrollBox.setScrollY(offsetY);
        fragmentOffsetY -= scrollBox.scrollY();
      }

      if (scPosition == null) {
        scPosition = newPosition;
      }
      currentContext = currentContext.parentContext();
    }
  }

}
