package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;

public class LineBoxFragment extends LayoutFragment {

  private final SinglyLinkedList<LayoutFragment> fragments;

  private LayoutFragment parentFragment;

  public LineBoxFragment(
    float width, float height, SinglyLinkedList<LayoutFragment> fragments
  ) {
    super(width, height);
    this.fragments = fragments;

    SinglyLinkedList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      curNode.item().setParent(this);
      curNode = curNode.next();
    }
  }

  // This constructor is for testing, not normal code use
  public LineBoxFragment(
    float x, float y, float width, float height, List<LayoutFragment> fragments
  ) {
    this(width, height, SinglyLinkedList.fromList(fragments));
    setPos(x, y);
  }

  public SinglyLinkedList<LayoutFragment> fragments() {
    return this.fragments;
  }

  public void setParent(LayoutFragment parent) {
    this.parentFragment = parent;
  }

  @Override
  public float layerX() {
    assert parentFragment != null;
    return parentFragment.layerX() + contentX();
  }

  @Override
  public float layerY() {
    assert parentFragment != null;
    return parentFragment.layerY() + contentY();
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[LineBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]");
    
    SinglyLinkedList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      textBuilder.append("\n\t" + curNode.item().toString().replace("\n", "\n\t"));
      curNode = curNode.next();
    }

    return textBuilder.toString();
  }

}
