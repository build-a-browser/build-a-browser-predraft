package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;

public class UnmanagedBoxFragment extends BoxFragment {

  public UnmanagedBoxFragment(float width, float height, ElementBox box, BoxPainter painter) {
    super(width, height, box, painter);
  }

  public UnmanagedBoxFragment(float x, float y, float width, float height, ElementBox box) {
    super(width, height, box, null);
    setPos(x, y);
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]";
  }

}
