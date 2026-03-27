package net.buildabrowser.babbrowser.render.layout;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class CachedLayoutResult implements IntrusiveList<CachedLayoutResult> {
  
  private final LayoutConstraint widthConstraint;
  private final LayoutConstraint heightConstraint;
  private final float widthResult;
  private final float heightResult;

  private CachedLayoutResult next;

  public CachedLayoutResult(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    float widthResult,
    float heightResult
  ) {
    this.widthConstraint = widthConstraint;
    this.heightConstraint = heightConstraint;
    this.widthResult = widthResult;
    this.heightResult = heightResult;
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
    return this.widthResult;
  }

  public float height() {
    return this.heightResult;
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
    float widthResult,
    float heightResult,
    CachedLayoutResult next
  ) {
    CachedLayoutResult result = new CachedLayoutResult(widthConstraint, heightConstraint, widthResult, heightResult);
    result.setNext(next);
    return result;
  }

}
