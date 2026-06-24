package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class ButtonInputFragment extends BaseInputFragment<ButtonInputFragment> {

  private final UnmanagedBoxFragment<?> innerFragment;

  public ButtonInputFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    UnmanagedBoxFragment<?> innerFragment
  ) {
    super(width, height, inkWidth, inkHeight, box);
    this.innerFragment = innerFragment;
  }

  public UnmanagedBoxFragment<?> innerFragment() {
    return this.innerFragment;
  }

}
