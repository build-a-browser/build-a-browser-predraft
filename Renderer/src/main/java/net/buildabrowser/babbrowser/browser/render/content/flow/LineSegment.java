package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;

public class LineSegment {

  private final ElementBox box;
  private SinglyLinkedList<LayoutFragment> fragments;
  private SinglyLinkedList<LayoutFragment> nextFragment;

  public LineSegment(ElementBox box) {
    this.box = box;
  }

  public ElementBox box() {
    return this.box;
  }

  public SinglyLinkedList<LayoutFragment> fragments() {
    return this.fragments;
  }

  public float width() {
    float width = 0;

    SinglyLinkedList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      width += curNode.item().borderWidth();
      curNode = curNode.next();
    }

    return width;
  }

  public float height() {
    float height = 0;

    SinglyLinkedList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      height = Math.max(height, curNode.item().borderHeight());
      curNode = curNode.next();
    }

    return height;
  }

  public void addFragment(LayoutFragment managedBoxFragment) {
    SinglyLinkedList<LayoutFragment> newFragment = SinglyLinkedList.add(nextFragment, managedBoxFragment);
    if (fragments == null) {
      fragments = newFragment;
    }

    nextFragment = nextFragment == null ? newFragment : nextFragment.next();
  }

}