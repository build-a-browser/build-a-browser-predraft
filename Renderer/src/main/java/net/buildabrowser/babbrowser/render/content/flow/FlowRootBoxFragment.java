package net.buildabrowser.babbrowser.render.content.flow;

import java.util.List;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;

public class FlowRootBoxFragment extends UnmanagedBoxFragment {

  private final ManagedBoxFragment rootFragment;
  private final List<BoxFragment> floats;

  public FlowRootBoxFragment(
    float usedWidth, float usedHeight,
    float inkWidth, float inkHeight,
    ElementBox rootBox, ManagedBoxFragment rootFragment,
    List<BoxFragment> floats
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight, rootBox,
      FlowRootContentPainter.FLOW_ROOT_PAINTER);
    this.rootFragment = rootFragment;
    this.floats = floats;
  }

  public ManagedBoxFragment rootFragment() {
    return this.rootFragment;
  }

  public List<BoxFragment> floats() {
    return this.floats;
  }
  
}
