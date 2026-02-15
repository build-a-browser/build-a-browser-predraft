package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class LineSegment {

  private final ElementBox box;
  private LayoutFragment fragments;
  private LayoutFragment nextFragment;

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
    float width = 0;

    LayoutFragment curNode = fragments;
    while (curNode != null) {
      width += curNode.borderWidth();
      curNode = curNode.next();
    }

    return width;
  }

  public float height() {
    float height = 0;

    LayoutFragment curNode = fragments;
    while (curNode != null) {
      height = Math.max(height, curNode.borderHeight());
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
  }

}