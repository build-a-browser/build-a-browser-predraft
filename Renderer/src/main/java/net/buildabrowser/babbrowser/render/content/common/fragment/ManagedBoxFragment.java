package net.buildabrowser.babbrowser.render.content.common.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

public class ManagedBoxFragment extends BoxFragment {

  private final LayoutFragment fragments;

  public ManagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, BoxPainter painter,
    LayoutFragment fragments
  ) {
    super(
      width, height,
      inkWidth, inkHeight,
      box, painter);
    this.fragments = fragments;
  }

  // Constructor used by tests, don't use in normal code
  public ManagedBoxFragment(
    float x, float y,
    float width, float height,
    ElementBox box, List<LayoutFragment> fragments
  ) {
    super(width, height, width, height, box, UnreachableBoxPainter.create(box.element()));
    this.fragments = IntrusiveList.fromList(fragments);
    setPos(x, y);
  }

  public LayoutFragment fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[ManagedBoxFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "]]");

    IntrusiveList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      textBuilder.append("\n\t" + curNode.toString().replace("\n", "\n\t"));
      curNode = curNode.next();
    }
    return textBuilder.toString();
  }

}
