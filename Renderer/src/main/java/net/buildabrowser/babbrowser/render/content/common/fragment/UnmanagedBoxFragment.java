package net.buildabrowser.babbrowser.render.content.common.fragment;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

public class UnmanagedBoxFragment extends BoxFragment {

  public UnmanagedBoxFragment(float width, float height, ElementBox box, BoxPainter painter) {
    super(width, height, box, painter);
  }

  public UnmanagedBoxFragment(float width, float height, ElementBox box) {
    super(width, height, box);
  }

  public UnmanagedBoxFragment(float x, float y, float width, float height, ElementBox box) {
    super(width, height, box, new UnreachableBoxPainter());
    setPos(x, y);
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]";
  }

}
