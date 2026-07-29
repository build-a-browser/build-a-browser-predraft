package net.buildabrowser.babbrowser.renderer.fragment.textarea;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.input.BaseInputFragment;

public abstract class TextAreaBoxFragment extends BaseInputFragment<TextAreaBoxFragment> {

  public TextAreaBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box
  ) {
    super(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box);
  }
  
}
