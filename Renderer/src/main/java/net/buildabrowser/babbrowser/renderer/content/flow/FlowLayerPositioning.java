package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;

public final class FlowLayerPositioning {
  
  private FlowLayerPositioning() {}

  public static void positionLayers(
    float layerX, float layerY,
    ManagedBoxFragment<?> rootFragment,
    FloatTracker floatTracker
  ) {
    rootFragment.setLayerPos(layerX, layerY);

    ElementBox rootBox = rootFragment.box();
    recursePositionLayers(
      layerX, layerY, 0, 0,
      rootFragment, rootBox.stackingContext());

    for (BoxFragment<?> floatFragment: floatTracker.allFloats()) {
      positionFloatLayers(
        layerX + floatFragment.layerX(Measurement.BORDER),
        layerY + floatFragment.layerY(Measurement.BORDER),
        floatFragment, rootBox.stackingContext());
    }
  }

  private static void recursePositionLayers(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    LayoutFragment fragment, StackingContext refContext
  ) {
    switch (fragment) {
      case TextFragment _1 -> {}
      case PosRefBoxFragment posRef -> {
        posRef.box().alterDimensions(false, d -> d.setStaticPosition(layerX, layerY));
      }
      case LineBoxFragment lineBoxFragment -> recursePositionLineBoxFragment(
        layerX, layerY, layerStartX, layerStartY, refContext, lineBoxFragment);
      case ManagedBoxFragment<?> boxFragment -> recursePositionManagedBoxFragment(
        layerX, layerY, layerStartX, layerStartY, fragment, refContext, boxFragment);
      case UnmanagedBoxFragment<?> boxFragment -> recursePositionUnmanagedBoxFragment(
        layerX, layerY, refContext, boxFragment);
      case FloatRefFragment floatRefFragment -> floatRefFragment.setFloatLayerStartPos(layerStartX, layerStartY);

      default -> throw new UnsupportedOperationException("Don't recognize fragment type!");
    }
  }

  private static void recursePositionLineBoxFragment(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    StackingContext refContext, LineBoxFragment lineBoxFragment
  ) {
    LayoutFragment child = lineBoxFragment.fragments();
    while (child != null) {
      recursePositionLayers(
        layerX + child.posX(Measurement.BORDER),
        layerY + child.posY(Measurement.BORDER),
        layerStartX, layerStartY,
        child, refContext);
      child = child.next();
    }
  }

  private static void recursePositionManagedBoxFragment(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    LayoutFragment fragment, StackingContext refContext,
    ManagedBoxFragment<?> boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      boxFragment.box().stackingContext().positionFragment(
        layerX, layerY, boxFragment,
        (childLayerX, childLayerY) -> recursePositionManagedBoxFragmentInner(
          childLayerX, childLayerY,
          layerStartX + layerX - childLayerX,
          layerStartY + layerY - childLayerY,
          fragment, boxFragment));
    } else {
      boxFragment.setLayerPos(layerX, layerY);
      recursePositionManagedBoxFragmentInner(
        layerX, layerY,
        layerStartX, layerStartY,
        fragment, boxFragment);
    }
  }

  private static void recursePositionManagedBoxFragmentInner(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    LayoutFragment fragment,
    ManagedBoxFragment<?> boxFragment
  ) {
    float offsetX = layerX + (fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER));
    float offsetY = layerY + (fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER));
    
    LayoutFragment child = boxFragment.fragments();
    while (child != null) {
      recursePositionLayers(
        offsetX + child.posX(Measurement.BORDER),
        offsetY + child.posY(Measurement.BORDER),
        layerStartX, layerStartY,
        child, boxFragment.box().stackingContext());
      child = child.next();
    }
  }

  private static void recursePositionUnmanagedBoxFragment(
    float layerX, float layerY,
    StackingContext refContext,
    UnmanagedBoxFragment<?> boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      boxFragment.box().stackingContext().positionFragment(
        layerX, layerY, boxFragment,
        boxFragment.box().content()::positionLayers);
    } else {
      boxFragment.setLayerPos(layerX, layerY);
      boxFragment.box().content().positionLayers(layerX, layerY);
    }
  }

  // TODO: Ensure this logic is correct. It's really hacky, just tuned it until it seemed to work
  // If you nest an overflow container in a float in a relative box, this needs to handle that
  // But changes should also not cause regressions on Acid2
  private static void positionFloatLayers(
    float layerX, float layerY,
    BoxFragment<?> boxFragment,
    StackingContext refContext
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      boxFragment.box().stackingContext().positionNormalizedFragment(
        layerX, layerY, boxFragment,
        boxFragment.box().content()::positionLayers);
    } else {
      boxFragment.setLayerPos(layerX, layerY);
      boxFragment.box().content().positionLayers(layerX, layerY);
    }
  }

}
