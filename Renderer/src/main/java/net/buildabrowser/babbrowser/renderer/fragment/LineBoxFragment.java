package net.buildabrowser.babbrowser.renderer.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class LineBoxFragment extends LayoutFragment {

  private final LayoutFragment fragments;

  public LineBoxFragment(
    float width, float height, LayoutFragment fragments
  ) {
    super(width, height);
    this.fragments = fragments;
  }

  // This constructor is for testing, not normal code use
  public LineBoxFragment(
    float x, float y, float width, float height, List<LayoutFragment> fragments
  ) {
    this(width, height, IntrusiveList.fromList(fragments));
    setPos(x, y);
  }

  public LayoutFragment fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[LineBoxFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "]]");
    
    IntrusiveList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      textBuilder.append("\n\t" + curNode.toString().replace("\n", "\n\t"));
      curNode = curNode.next();
    }

    return textBuilder.toString();
  }

}
