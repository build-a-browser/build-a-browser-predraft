package net.buildabrowser.babbrowser.renderer.composite;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;

// Needed to avoid destroying data in the fragment, used by other stages
public class CompositeLayerEntry implements IntrusiveList<CompositeLayerEntry> {
  
  private final float offsetX;
  private final float offsetY;
  private final BoxFragment fragment;

  private CompositeLayerEntry nextEntry;

  public CompositeLayerEntry(
    float offsetX, float offsetY, BoxFragment fragment
  ) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.fragment = fragment;
  }

  public BoxFragment fragment() {
    return this.fragment;
  }

  public float offsetX() {
    return this.offsetX;
  }

  public float offsetY() {
    return this.offsetY;
  }

  @Override
  public CompositeLayerEntry next() {
    return this.nextEntry;
  }

  @Override
  public void setNext(CompositeLayerEntry nextNode) {
    this.nextEntry = nextNode;
  }

}
