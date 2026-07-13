package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public abstract class TextInputFragment extends BaseInputFragment<TextInputFragment> {

  public TextInputFragment(
    float width, float height,
    float firstBaseline, float lastBaseline,
    float inkWidth, float inkHeight,
    ElementBox box
  ) {
    super(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box);
  }
  
}
