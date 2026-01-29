package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;

public class BlockFormattingContext {

  private final ElementBox elementBox;
  private final LayoutConstraint innerWidthConstraint;
  private final LayoutConstraint innerHeightConstraint;
  private final FlowRootContent rootContent;

  private final List<LayoutFragment> fragments;

  private int width;
  private int y;

  private BlockFormattingContext collapseContext;
  private int maxMargin = 0;
  private int minMargin = 0;

  public BlockFormattingContext(
    ElementBox elementBox,
    LayoutConstraint innerWidthConstraint,
    LayoutConstraint innerHeightConstraint,
    FlowRootContent rootContent,
    BlockFormattingContext collapseContext
  ) {
    this.elementBox = elementBox;
    this.innerWidthConstraint = innerWidthConstraint;
    this.innerHeightConstraint = innerHeightConstraint;
    this.rootContent = rootContent;
    this.collapseContext = collapseContext;

    this.fragments = new ArrayList<>();
  }

  public int currentY() {
    return this.y;
  }

  public void increaseY(int yInc) {
    this.y += yInc;
  }

  public void minWidth(int minWidth) {
    this.width = Math.max(width, minWidth);
  }

  public void recordMargin(int margin) {
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

    int amount = maxMargin + minMargin;
    rootContent.floatTracker().adjustPos(0, amount);
    increaseY(amount);
    this.maxMargin = 0;
    this.minMargin = 0;
  }

  public int currentMaxMargin() {
    return this.maxMargin;
  }

  public int currentMinMargin() {
    return this.minMargin;
  }

  public void addFragment(LayoutFragment newFragment) {
    this.fragments.add(newFragment);
  }

  public LayoutConstraint innerWidthConstraint() {
    return this.innerWidthConstraint;
  }

  public LayoutConstraint innerHeightConstraint() {
    return this.innerHeightConstraint;
  }

  public ManagedBoxFragment close(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    int usedWidth = LayoutUtil.constraintOrDim(widthConstraint, width);
    int usedHeight = LayoutUtil.constraintOrDim(heightConstraint, y);
    return new ManagedBoxFragment(
      usedWidth, usedHeight, elementBox,
      FlowRootContentPainter.FLOW_BLOCK_PAINTER, fragments);
  }

}