package net.buildabrowser.babbrowser.renderer.fragment.flexbox;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class FlexBoxFragment extends UnmanagedBoxFragment<FlexBoxFragment> {

  private UnmanagedBoxFragment<?> fragments;

  public FlexBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, UnmanagedBoxFragment<?> fragments
  ) {
    super(width, height, inkWidth, inkHeight, box);
    this.fragments = fragments;
  }

  public UnmanagedBoxFragment<?> fragments() {
    return this.fragments;
  }
  
}
