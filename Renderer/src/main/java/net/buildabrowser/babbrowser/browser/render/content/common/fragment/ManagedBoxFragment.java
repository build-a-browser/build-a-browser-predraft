package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;

public class ManagedBoxFragment extends BoxFragment {

  private final SinglyLinkedList<LayoutFragment> fragments;

  public ManagedBoxFragment(
    float width, float height, ElementBox box,
    BoxPainter painter,
    SinglyLinkedList<LayoutFragment> fragments
  ) {
    super(width, height, box, painter);
    this.fragments = fragments;

    SinglyLinkedList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      curNode.item().setParent(this);
      curNode = curNode.next();
    }
  }

  // Constructor used by tests, don't use in normal code
  public ManagedBoxFragment(
    float x, float y, float width, float height, ElementBox box,
    List<LayoutFragment> fragments
  ) {
    super(width, height, box, null);
    this.fragments = SinglyLinkedList.fromList(fragments);

    for (LayoutFragment fragment: fragments) {
      fragment.setParent(this);
    }
    setPos(x, y);
  }

  public SinglyLinkedList<LayoutFragment> fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[ManagedBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]");

    SinglyLinkedList<LayoutFragment> curNode = fragments;
    while (curNode != null) {
      textBuilder.append("\n\t" + curNode.item().toString().replace("\n", "\n\t"));
      curNode = curNode.next();
    }
    return textBuilder.toString();
  }

}
