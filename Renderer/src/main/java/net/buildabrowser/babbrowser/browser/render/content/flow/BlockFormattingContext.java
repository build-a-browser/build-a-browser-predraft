package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class BlockFormattingContext {

  private final ElementBox elementBox;
  private final LayoutConstraint innerWidthConstraint;
  private final LayoutConstraint innerHeightConstraint;

  private LayoutFragment fragments;
  private LayoutFragment nextFragment;

  private float width;
  private float y;

  private BlockFormattingContext parentContext;
  private BlockFormattingContext collapseContext;
  private float maxMargin = 0;
  private float minMargin = 0;

  public BlockFormattingContext(
    ElementBox elementBox,
    LayoutConstraint innerWidthConstraint,
    LayoutConstraint innerHeightConstraint,
    BlockFormattingContext parentContext,
    BlockFormattingContext collapseContext
  ) {
    this.elementBox = elementBox;
    this.innerWidthConstraint = innerWidthConstraint;
    this.innerHeightConstraint = innerHeightConstraint;
    this.parentContext = parentContext;
    this.collapseContext = collapseContext;
  }

  public float currentY() {
    return this.y;
  }

  public void increaseY(float yInc) {
    this.y += yInc;
  }

  public float estimateAbsX() {
    if (parentContext == null) {
      return 0;
    } else {
      float[] margin = elementBox.dimensions().getComputedMargin();
      float[] border = elementBox.dimensions().getComputedBorder();
      float[] padding = elementBox.dimensions().getComputedPadding();
      return margin[2] + border[2] + padding[2] + parentContext.estimateAbsX();
    }
  }

  public float estimateAbsY() {
    if (parentContext == null) {
      return this.y;
    } else {
      float[] border = elementBox.dimensions().getComputedBorder();
      float[] padding = elementBox.dimensions().getComputedPadding();
      return this.y + border[0] + padding[0] + parentContext.estimateAbsY();
    }
  }

  public void minWidth(float minWidth) {
    this.width = Math.max(width, minWidth);
  }

  public void recordMargin(float margin) {
    if (this.collapseContext != null) {
      collapseContext.recordMargin(margin);
      return;
    }

    if (margin > 0) {
      maxMargin = Math.max(maxMargin, margin);
    } else {
      minMargin = Math.min(minMargin, margin);
    }
  }

  public void collapse() {
    if (collapseContext != null) {
      collapseContext.collapse();
      this.collapseContext = null;
      return;
    }

    float amount = maxMargin + minMargin;
    increaseY(amount);
    this.maxMargin = 0;
    this.minMargin = 0;
  }

  public float currentMaxMargin() {
    return this.maxMargin;
  }

  public float currentMinMargin() {
    return this.minMargin;
  }

  public void addFragment(LayoutFragment fragment) {
    if (fragments == null) {
      fragments = fragment;
    } else {
      IntrusiveList.add(nextFragment, fragment);
    }

    nextFragment = fragment;
  }

  public LayoutConstraint innerWidthConstraint() {
    return this.innerWidthConstraint;
  }

  public LayoutConstraint innerHeightConstraint() {
    return this.innerHeightConstraint;
  }

  public ManagedBoxFragment close(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    float preclampWidth = LayoutUtil.constraintOrDim(widthConstraint, width);
    float preclampHeight = LayoutUtil.constraintOrDim(heightConstraint, y);
    
    // In case it wasn't originally resolved. Passing AUTO should be fine because if the parent is
    // definite it should have already resolved anyways. Note that this will not resolve child percentages
    // if the original clamp was not definite, that is intentional.
    // However, we must preserve the prelayout state
    float usedWidth = SizingUtil.clampWidth(
      widthConstraint.isBounded() ? LayoutConstraint.AUTO : widthConstraint,
      elementBox, LayoutConstraint.of(preclampWidth)).value();
    float usedHeight = SizingUtil.clampHeight(
      heightConstraint.isBounded() ? LayoutConstraint.AUTO : heightConstraint,
      elementBox, LayoutConstraint.of(preclampHeight)).value();

    return new ManagedBoxFragment(
      usedWidth, usedHeight, elementBox,
      FlowRootContentPainter.FLOW_BLOCK_PAINTER, fragments);
  }

}