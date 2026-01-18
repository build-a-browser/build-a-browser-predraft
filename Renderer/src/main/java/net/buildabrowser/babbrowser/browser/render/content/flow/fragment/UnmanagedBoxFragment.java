package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class UnmanagedBoxFragment extends FlowFragment {

  private final ElementBox box;

  public UnmanagedBoxFragment(int width, int height, ElementBox box) {
    super(width, height);
    this.box = box;
  }

  public UnmanagedBoxFragment(int x, int y, int width, int height, ElementBox box) {
    super(width, height);
    setPos(x, y);
    this.box = box;
  }

  public ElementBox box() {
    return this.box;
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=[" + posX() + ", " + posY() + "] size=[" + width() + "x" + height() + "]]";
  }

}
