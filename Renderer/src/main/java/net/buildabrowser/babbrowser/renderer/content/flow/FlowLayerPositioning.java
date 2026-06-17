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

    float offsetX = layerX + (rootFragment.posX(Measurement.CONTENT) - rootFragment.posX(Measurement.BORDER));
    float offsetY = layerY + (rootFragment.posY(Measurement.CONTENT) - rootFragment.posY(Measurement.BORDER));
    for (BoxFragment<?> floatFragment: floatTracker.allFloats()) {
      positionFloatLayers(offsetX, offsetY, floatFragment, rootBox.stackingContext());
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
      refContext = boxFragment.box().stackingContext();
      refContext.addFragment(layerX, layerY, boxFragment);
      layerStartX += layerX;
      layerStartY += layerY;
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
      refContext = boxFragment.box().stackingContext();
      refContext.addFragment(layerX, layerY, boxFragment);
      layerX = 0;
      layerY = 0;
    }
    boxFragment.setLayerPos(layerX, layerY);
    boxFragment.box().content().positionLayers(layerX, layerY);
  }

  // TODO: Ensure this logic is correct. It's really hacky, just tuned it until it seemed to work
  // If you nest an overflow container in a float in a relative box, this needs to handle that
  // But changes should also not cause regressions on Acid2
  private static void positionFloatLayers(
    float layerX, float layerY,
    BoxFragment<?> fragment,
    StackingContext refContext
  ) {  
    float floatX = fragment.posX(Measurement.BORDER);
    float floatY = fragment.posY(Measurement.BORDER);
    if (
      fragment instanceof UnmanagedBoxFragment floatFragment
      && fragment.box().stackingContext() != refContext
    ) {
      // Set via FloatRefFragment
      float floatLayerX = fragment.layerX(Measurement.BORDER);
      float floatLayerY = fragment.layerY(Measurement.BORDER);
      fragment.box().stackingContext().addFragment(
        layerX + floatX,
        layerY + floatY, fragment);
      floatFragment.box().content().positionLayers(
        layerX + floatLayerX, layerY + floatLayerY);
    } else {
      recursePositionLayers(
        layerX + floatX, layerY + floatY,
        0, 0,
        fragment, refContext);
    }
  }

}
