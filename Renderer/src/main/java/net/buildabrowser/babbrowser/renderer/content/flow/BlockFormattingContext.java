package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowBlockBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class BlockFormattingContext {

  private final ElementBox elementBox;
  private final LayoutConstraint innerWidthConstraint;
  private final LayoutConstraint innerHeightConstraint;

  private LayoutFragment fragments;
  private LayoutFragment nextFragment;

  private float width;
  private float y;

  private float inkWidth;
  private float inkY;

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

  public void increaseY(float yInc, float yInkInc) {
    this.inkY = Math.max(inkY, y + yInkInc);
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

  public void minWidth(float minWidth, float inkWidth) {
    assert !Float.isNaN(minWidth);
    assert !Float.isNaN(inkWidth);
    this.width = Math.max(width, minWidth);
    this.inkWidth = Math.max(this.inkWidth, inkWidth);
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
    increaseY(amount, amount);
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

  public FlowBlockBoxFragment close(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    return close(widthConstraint, heightConstraint, 0, 0);
  }

  public FlowBlockBoxFragment close(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    float contributionW,
    float contributionH
  ) {
    float preclampWidth = LayoutUtil.constraintOrDim(widthConstraint, Math.max(width, contributionW));
    float preclampHeight = LayoutUtil.constraintOrDim(heightConstraint, Math.max(y, contributionH));
    
    // In case it wasn't originally resolved. Passing AUTO should be fine because if the parent is
    // definite it should have already resolved anyways. Note that this will not resolve child percentages
    // if the original clamp was not definite, that is intentional.
    // However, we must preserve the prelayout state
    float usedWidth = SizingWidthUtil.clampWidth(
      widthConstraint.isBounded() ? LayoutConstraint.AUTO : widthConstraint,
      elementBox, LayoutConstraint.of(preclampWidth)).value();
    float usedHeight = SizingHeightUtil.clampHeight(
      heightConstraint.isBounded() ? LayoutConstraint.AUTO : heightConstraint,
      elementBox, LayoutConstraint.of(preclampHeight)).value();

    assert !Float.isNaN(inkWidth);
    assert !Float.isNaN(contributionW);
    FragmentFactory fragmentFactory = elementBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createFlowBlockBoxFragment(
      usedWidth, usedHeight,
      Math.max(inkWidth, contributionW),
      Math.max(inkY, contributionH),
      elementBox, fragments);
  }

  public PropertyContainer properties() {
    return elementBox.properties();
  }

}