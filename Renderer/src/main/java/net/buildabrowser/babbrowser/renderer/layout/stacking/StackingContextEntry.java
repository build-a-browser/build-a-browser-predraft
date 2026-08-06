package net.buildabrowser.babbrowser.renderer.layout.stacking;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;

// Needed to avoid destroying data in the fragment, used by other stages
public class StackingContextEntry implements IntrusiveList<StackingContextEntry> {
  
  private final float offsetX;
  private final float offsetY;
  private final BoxFragment<?> fragment;

  private StackingContextEntry nextEntry;

  public StackingContextEntry(
    float offsetX, float offsetY, BoxFragment<?> fragment
  ) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.fragment = fragment;
  }

  public BoxFragment<?> fragment() {
    return this.fragment;
  }

  public float offsetX() {
    return this.offsetX;
  }

  public float offsetY() {
    return this.offsetY;
  }

  @Override
  public StackingContextEntry next() {
    return this.nextEntry;
  }

  @Override
  public void setNext(StackingContextEntry nextNode) {
    this.nextEntry = nextNode;
  }

}
