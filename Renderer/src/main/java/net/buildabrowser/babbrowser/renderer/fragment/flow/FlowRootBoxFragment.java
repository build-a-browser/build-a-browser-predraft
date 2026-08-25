package net.buildabrowser.babbrowser.renderer.fragment.flow;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class FlowRootBoxFragment extends UnmanagedBoxFragment<FlowRootBoxFragment> {

  private final List<BoxFragment<?>> floats;

  public FlowRootBoxFragment(
    float usedWidth, float usedHeight,
    float inkWidth, float inkHeight,
    ElementBox rootBox, ManagedBoxFragment<?> rootFragment,
    List<BoxFragment<?>> floats
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight,
      // TODO: How do floats affect this?
      rootFragment.firstBaseline(Measurement.CONTENT),
      rootFragment.lastBaseline(Measurement.CONTENT),
      rootBox, rootFragment);
    this.floats = floats;
  }

  public ManagedBoxFragment<?> rootFragment() {
    return (ManagedBoxFragment<?>) innerFragment();
  }

  public List<BoxFragment<?>> floats() {
    return this.floats;
  }
  
}
