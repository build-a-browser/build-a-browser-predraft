package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public abstract class ManagedBoxFragment<T extends ManagedBoxFragment<T>> extends BoxFragment<T> {

  private final LayoutFragment fragments;

  public ManagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, 
    LayoutFragment fragments
  ) {
    super(
      width, height,
      inkWidth, inkHeight,
      box);
    this.fragments = fragments;
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
