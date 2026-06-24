package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public abstract class HiddenInputFragment extends BaseInputFragment<HiddenInputFragment> {

  public HiddenInputFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box
  ) {
    super(width, height, inkWidth, inkHeight, box);
  }
  
}
