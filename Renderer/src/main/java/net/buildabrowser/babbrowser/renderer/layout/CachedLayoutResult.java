package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public class CachedLayoutResult implements IntrusiveList<CachedLayoutResult> {
  
  private final LayoutConstraint widthConstraint;
  private final LayoutConstraint heightConstraint;
  private final UnmanagedBoxFragment<?> layoutFragment;

  private CachedLayoutResult next;

  public CachedLayoutResult(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    UnmanagedBoxFragment<?> layoutFragment
  ) {
    this.widthConstraint = widthConstraint;
    this.heightConstraint = heightConstraint;
    this.layoutFragment = layoutFragment;
  }

  @Override
  public CachedLayoutResult next() {
    return next;
  }

  @Override
  public void setNext(CachedLayoutResult nextNode) {
    this.next = nextNode;
  }

  public float width() {
    return layoutFragment.width(Measurement.CONTENT);
  }

  public float height() {
    return layoutFragment.height(Measurement.CONTENT);
  }

  public UnmanagedBoxFragment<?> fragment() {
    return this.layoutFragment;
  }

  public boolean applies(
    LayoutConstraint refWidthConstraint, LayoutConstraint refHeightConstraint
  ) {
    return
      this.widthConstraint.equals(refWidthConstraint)
      && this.heightConstraint.equals(refHeightConstraint);
  }

  public static CachedLayoutResult create(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    UnmanagedBoxFragment<?> layoutFragment,
    CachedLayoutResult next
  ) {
    CachedLayoutResult result = new CachedLayoutResult(widthConstraint, heightConstraint, layoutFragment);
    result.setNext(next);
    return result;
  }

}
