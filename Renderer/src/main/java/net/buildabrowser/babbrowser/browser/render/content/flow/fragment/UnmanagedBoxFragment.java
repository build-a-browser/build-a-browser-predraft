package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class UnmanagedBoxFragment extends FlowBoxFragment {

  public UnmanagedBoxFragment(int width, int height, ElementBox box) {
    super(width, height, box);
  }

  public UnmanagedBoxFragment(int x, int y, int width, int height, ElementBox box) {
    super(width, height, box);
    setPos(x, y);
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]";
  }

}
