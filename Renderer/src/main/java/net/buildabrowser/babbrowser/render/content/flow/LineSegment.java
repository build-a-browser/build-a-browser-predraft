package net.buildabrowser.babbrowser.render.content.flow;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;

public class LineSegment {

  private final ElementBox box;
  private LayoutFragment fragments;
  private LayoutFragment nextFragment;
  private float width = 0;
  private float inkWidth = 0;

  public LineSegment(ElementBox box) {
    this.box = box;
  }

  public ElementBox box() {
    return this.box;
  }

  public LayoutFragment fragments() {
    return this.fragments;
  }

  public float width() {
    return this.width;
  }

  public float inkWidth() {
    return this.inkWidth;
  }

  public float height() {
    float height = 0;

    LayoutFragment curNode = fragments;
    while (curNode != null) {
      height = Math.max(height, curNode.height(Measurement.BORDER));
      curNode = curNode.next();
    }

    return height;
  }

  public float inkHeight() {
    float height = 0;

    LayoutFragment curNode = fragments;
    while (curNode != null) {
      height = Math.max(height, curNode.inkHeight(Measurement.BORDER));
      curNode = curNode.next();
    }

    return height;
  }

  public void addFragment(LayoutFragment managedBoxFragment) {
    LayoutFragment newFragment = IntrusiveList.add(nextFragment, managedBoxFragment);
    if (fragments == null) {
      fragments = newFragment;
    }

    nextFragment = nextFragment == null ? newFragment : nextFragment.next();

    inkWidth = Math.max(
      width + managedBoxFragment.inkWidth(Measurement.BORDER),
      inkWidth);
    width += managedBoxFragment.width(Measurement.BORDER);
  }

}