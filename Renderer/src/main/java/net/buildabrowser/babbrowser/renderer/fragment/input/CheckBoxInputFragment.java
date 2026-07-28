package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public abstract class CheckBoxInputFragment extends BaseInputFragment<CheckBoxInputFragment> {

  public CheckBoxInputFragment(
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
