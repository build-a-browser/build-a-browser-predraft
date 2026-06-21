package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class BaseInputFragment
  <T extends BaseInputFragment<T>> extends UnmanagedBoxFragment<T> {

  public BaseInputFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box
  ) {
    super(width, height, inkWidth, inkHeight, box);
  }
  
}
