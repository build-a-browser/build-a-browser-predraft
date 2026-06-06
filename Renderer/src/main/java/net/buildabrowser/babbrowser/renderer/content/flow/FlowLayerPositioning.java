package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;

public final class FlowLayerPositioning {
  
  private FlowLayerPositioning() {}

  public static void positionLayers(
    float layerX, float layerY,
    ManagedBoxFragment rootFragment,
    FloatTracker floatTracker
  ) {
    rootFragment.setLayerPos(layerX, layerY);

    ElementBox rootBox = rootFragment.box();
    recursePositionLayers(
      layerX, layerY,
      rootFragment, rootBox.stackingContext());

    float offsetX = layerX + (rootFragment.posX(Measurement.CONTENT) - rootFragment.posX(Measurement.BORDER));
    float offsetY = layerY + (rootFragment.posY(Measurement.CONTENT) - rootFragment.posY(Measurement.BORDER));

    for (LayoutFragment floatFragment: floatTracker.allFloats()) {
      recursePositionLayers(
        offsetX + floatFragment.posX(Measurement.BORDER),
        offsetY + floatFragment.posY(Measurement.BORDER),
        floatFragment, rootBox.stackingContext());
    }
  }

  private static void recursePositionLayers(
    float layerX, float layerY, LayoutFragment fragment, StackingContext refContext
  ) {
    switch (fragment) {
      case TextFragment _1 -> {}
      case PosRefBoxFragment posRef -> {
        posRef.box().alterDimensions(false, d -> d.setStaticPosition(layerX, layerY));
      }
      case LineBoxFragment lineBoxFragment -> recursePositionLineBoxFragment(
        layerX, layerY, refContext, lineBoxFragment);
      case ManagedBoxFragment boxFragment -> recursePositionManagedBoxFragment(
        layerX, layerY, fragment, refContext, boxFragment);
      case UnmanagedBoxFragment boxFragment -> recursePositionUnmanagedBoxFragment(
        layerX, layerY, refContext, boxFragment);

      default -> throw new UnsupportedOperationException("Don't recognize fragment type!");
    }
  }

  private static void recursePositionLineBoxFragment(
    float layerX, float layerY, StackingContext refContext, LineBoxFragment lineBoxFragment
  ) {
    LayoutFragment child = lineBoxFragment.fragments();
    while (child != null) {
      recursePositionLayers(
        layerX + child.posX(Measurement.BORDER),
        layerY + child.posY(Measurement.BORDER),
        child, refContext);
      child = child.next();
    }
  }

  private static void recursePositionManagedBoxFragment(
    float layerX, float layerY, LayoutFragment fragment, StackingContext refContext,
    ManagedBoxFragment boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      refContext = boxFragment.box().stackingContext();
      refContext.addFragment(layerX, layerY, boxFragment);
      layerX = 0;
      layerY = 0;
    }
    boxFragment.setLayerPos(layerX, layerY);

    float offsetX = layerX + (fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER));
    float offsetY = layerY + (fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER));
    
    LayoutFragment child = boxFragment.fragments();
    while (child != null) {
      recursePositionLayers(
        offsetX + child.posX(Measurement.BORDER),
        offsetY + child.posY(Measurement.BORDER),
        child, refContext);
      child = child.next();
    }
  }

  private static void recursePositionUnmanagedBoxFragment(
    float layerX, float layerY, StackingContext refContext, UnmanagedBoxFragment boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      refContext = boxFragment.box().stackingContext();
      refContext.addFragment(layerX, layerY, boxFragment);
      layerX = 0;
      layerY = 0;
    }
    boxFragment.setLayerPos(layerX, layerY);
    boxFragment.box().content().positionLayers(layerX, layerY);
  }

}
