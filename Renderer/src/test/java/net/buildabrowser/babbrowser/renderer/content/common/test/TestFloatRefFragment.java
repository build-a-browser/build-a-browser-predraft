package net.buildabrowser.babbrowser.renderer.content.common.test;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;

public class TestFloatRefFragment extends LayoutFragment {

  private final ElementBox box;

  public TestFloatRefFragment(ElementBox box) {
    super(0, 0);
    this.box = box;
  }
  
  public ElementBox box() {
    return this.box;
  }

}
