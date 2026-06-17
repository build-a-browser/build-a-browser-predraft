package net.buildabrowser.babbrowser.renderer.fragment.flow;

import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;

public class FloatRefFragment extends LayoutFragment {

  private final BoxFragment<?> floatFragment;

  public FloatRefFragment(BoxFragment<?> floatFragment) {
    super(0, 0);
    this.floatFragment = floatFragment;
  }

  public void setFloatLayerStartPos(
    float layerStartX, float layerStartY
  ) {
    floatFragment.setLayerPos(
      floatFragment.posX(Measurement.BORDER) - layerStartX,
      floatFragment.posY(Measurement.BORDER) - layerStartY);
  }

  public BoxFragment<?> floatFragment() {
    return this.floatFragment;
  }
  
}