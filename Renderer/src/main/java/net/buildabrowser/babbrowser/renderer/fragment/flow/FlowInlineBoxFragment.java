package net.buildabrowser.babbrowser.renderer.fragment.flow;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;

public abstract class FlowInlineBoxFragment extends ManagedBoxFragment<FlowInlineBoxFragment> {

  public FlowInlineBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, LayoutFragment fragments
  ) {
    super(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, fragments);
  }
  
}
