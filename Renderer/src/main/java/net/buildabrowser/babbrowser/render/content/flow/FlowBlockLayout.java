package net.buildabrowser.babbrowser.render.content.flow;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.floats.ClearValue;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.box.TextBox;
import net.buildabrowser.babbrowser.render.content.common.BorderUtil;
import net.buildabrowser.babbrowser.render.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class FlowBlockLayout {

  private final FlowRootContent rootContent;

  private BlockFormattingContext rootContext;
  private BlockFormattingContext activeContext;

  public FlowBlockLayout(FlowRootContent rootContent) {
    this.rootContent = rootContent;
  }

  public void reset(
    ElementBox rootBox, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    this.rootContext = new BlockFormattingContext(
      rootBox, widthConstraint, heightConstraint, null,null);
    this.activeContext = rootContext;
  }

  public ManagedBoxFragment close(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    rootContext.collapse();
    return rootContext.close(widthConstraint, heightConstraint);
  }

  public BlockFormattingContext activeContext() {
    return this.activeContext;
  }
  
  public void addChildrenToBlock(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FlowInlineLayout inlineLayout = rootContent.inlineLayout();
    ActiveStyles boxStyles = box.activeStyles();

    boolean isInInline = false;
    for (Box childBox: box.childBoxes()) {
      if (childBox instanceof ElementBox elementBox) {
        // TODO: Maybe add a util method that groups all of this stuff
        BorderUtil.computeBorder(elementBox, widthConstraint);
        PaddingUtil.computePadding(elementBox, widthConstraint);
      }
      if (
        childBox instanceof ElementBox elementBox
        && !PositionUtil.affectsLayout(elementBox)
      ) {
        addPositionedToBlock(elementBox);
      } else if (
        childBox instanceof ElementBox elementBox
        && FlowUtil.isFloat(elementBox)
      ) {
        if (isInInline) {
          inlineLayout.stageInline(box.layoutContext(), childBox);
          continue;
        }
        
        activeContext.collapse();
        ackFloatClear(elementBox);
        UnmanagedBoxFragment floatFragment = FloatLayout.renderFloat(
          elementBox, widthConstraint, heightConstraint);
        FloatLayout.addFloat(rootContent, floatFragment, widthConstraint, heightConstraint, 0);
      } else if (FlowUtil.isBlockLevel(childBox)) {
        if (isInInline) {
          inlineLayout.stopInline(widthConstraint, heightConstraint, boxStyles);
          isInInline = false;
        }
        addToBlock((ElementBox) childBox, widthConstraint, heightConstraint);
      } else if (childBox instanceof TextBox textBox && textBox.text().isBlank()) {
        continue; // TODO: Check the actual spec-compliant way to handle this
      } else {
        activeContext.collapse();
        if (!isInInline) {
          inlineLayout.startInline(boxStyles, widthConstraint);
          isInInline = true;
        }
        inlineLayout.stageInline(box.layoutContext(), childBox);
      }
    }

    if (isInInline) {
      inlineLayout.stopInline(widthConstraint, heightConstraint, boxStyles);
    }
  }

  public void addToBlock(
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ackFloatClear(elementBox);
    if (FlowUtil.isInFlow(elementBox)) {
      addManagedBlockToBlock(elementBox, widthConstraint, heightConstraint);
    } else {
      addUnmanagedBlockToBlock(elementBox, widthConstraint, heightConstraint);
    }
  }

  private void addManagedBlockToBlock(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    BlockFormattingContext parentContext = activeContext;
    LayoutConstraint childWidthConstraint = FlowWidthUtil.evaluateNonReplacedBlockWidthAndMargins(
      parentWidthConstraint, childBox, 0, 0);
    LayoutConstraint childHeightConstraint = FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
      parentHeightConstraint, parentWidthConstraint, childBox);

    float[] margin = childBox.dimensions().getComputedMargin();

    parentContext.recordMargin(margin[0]);
    boolean collapseFirst = needsCollapsed(childBox, 0);
    if (collapseFirst) {
      parentContext.collapse();
    }
    BlockFormattingContext collapseContext = collapseFirst ? null : parentContext;
    BlockFormattingContext childContext = new BlockFormattingContext(
      childBox, childWidthConstraint, childHeightConstraint, parentContext, collapseContext);
    
    activeContext = childContext;

    addChildrenToBlock(childBox, childWidthConstraint, childHeightConstraint);

    boolean collapseAfter = needsCollapsed(childBox, 1);
    if (collapseAfter) {
      childContext.collapse();
    }

    ManagedBoxFragment newFragment = childContext.close(childWidthConstraint, childHeightConstraint);
    activeContext = parentContext;
    
    addFinishedFragment(newFragment, margin[2], parentWidthConstraint);
    
    if (!collapseAfter) {
      parentContext.recordMargin(childContext.currentMaxMargin());
      parentContext.recordMargin(childContext.currentMinMargin());
    }
    parentContext.recordMargin(margin[1]);
  }

  // TODO: Clean this up some
  private void addUnmanagedBlockToBlock(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    FloatTracker floatTracker = rootContent.floatTracker();
    float leftContent = floatTracker.lineStartPos();
    float rightContent = parentWidthConstraint.isBounded() ?
      floatTracker.lineEndPos(parentWidthConstraint) : 0;
    float rightExtraMargin = parentWidthConstraint.isBounded() ?
      parentWidthConstraint.value() - rightContent : 0;
    boolean adjustForFloats = parentWidthConstraint.isBounded()
      && (leftContent > 0 || rightContent != parentWidthConstraint.value());

    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        parentWidthConstraint, childBox) :
      FlowWidthUtil.evaluateNonReplacedBlockWidthAndMargins(
        parentWidthConstraint, childBox,
        leftContent, rightExtraMargin);
    LayoutConstraint childHeightConstraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint, childBox);

    float minClear = 0;
    if (
      adjustForFloats
      && childWidthConstraint.isBounded()
      && childWidthConstraint.value() > rightContent - leftContent
    ) {
      minClear = Math.max(
        floatTracker.clearedLineStartPosition(),
        floatTracker.clearedLineEndPosition());
      leftContent = 0;
      childWidthConstraint = childBox.isReplaced() ?
        FlowWidthUtil.determineBlockReplacedWidthAndMargins(
          parentWidthConstraint, childBox) :
        FlowWidthUtil.evaluateNonReplacedBlockWidthAndMargins(
          parentWidthConstraint, childBox,
          leftContent, rightExtraMargin);
    }

    float[] margin = childBox.dimensions().getComputedMargin();
    activeContext.recordMargin(Math.max(margin[0], minClear));
    activeContext.collapse();
    UnmanagedBoxFragment newFragment = parentWidthConstraint.isPreLayoutConstraint() ?
      new UnmanagedBoxFragment(
        FlowUtil.constraintWidth(childBox.dimensions(), childWidthConstraint),
        FlowUtil.constraintHeight(childBox.dimensions(), childHeightConstraint),
        childBox) :
      childBox.layout(childWidthConstraint, childHeightConstraint);

    activeContext.recordMargin(margin[1]);

    addFinishedFragment(newFragment, Math.max(margin[2], leftContent), parentWidthConstraint);
  }

  private void addPositionedToBlock(ElementBox childBox) {
    BlockFormattingContext parentContext = activeContext;

    float estimatedAboveMargin = parentContext.currentMaxMargin() + parentContext.currentMinMargin();
    // Don't bother marking the float tracker position, the child should establish a new one

    LayoutFragment newFragment = PositionLayout.layout(childBox);
    parentContext.addFragment(newFragment); // Still needed to set fragment parent

    float[] margin = childBox.dimensions().getComputedMargin();
    newFragment.setPos(margin[2], margin[0] + estimatedAboveMargin + parentContext.currentY());
  }

  public void addFinishedFragment(
    LayoutFragment newFragment, float posX, LayoutConstraint relatedConstraint
  ) {
    BlockFormattingContext parentContext = activeContext;
    newFragment.setPos(posX, parentContext.currentY());
    parentContext.increaseY(
      newFragment.height(Measurement.BORDER),
      newFragment.inkHeight(Measurement.BORDER));
    parentContext.minWidth(
      newFragment.posX(Measurement.MARGIN) + newFragment.width(Measurement.MARGIN),
      Math.max(
        newFragment.posX(Measurement.MARGIN) + newFragment.inkWidth(Measurement.MARGIN),
        // Since margin can be negative
        newFragment.posX(Measurement.BORDER) + newFragment.inkWidth(Measurement.BORDER)));
    parentContext.addFragment(newFragment);
    if (
      !relatedConstraint.isPreLayoutConstraint()
      && newFragment instanceof BoxFragment boxFragment
    ) {
      boxFragment.box().updatePositioningFragment(boxFragment);
    }
  }

  private void ackFloatClear(ElementBox elementBox) {
    CSSValue clearValue = elementBox.activeStyles().getProperty(CSSProperty.CLEAR);
    if (clearValue.equals(CSSValue.NONE)) return;
    float leftClear = clearValue.equals(ClearValue.RIGHT) ? 0 : rootContent.floatTracker().clearedLineStartPosition();
    float rightClear = clearValue.equals(ClearValue.LEFT) ? 0 : rootContent.floatTracker().clearedLineEndPosition();
    float totalClear = Math.max(leftClear, rightClear);
    activeContext.increaseY(totalClear, totalClear);
  }

  private boolean needsCollapsed(ElementBox box, int refIndex) {
    ElementBoxDimensions dimensions = box.dimensions();
    return
      dimensions.getComputedBorder()[refIndex] != 0
      || dimensions.getComputedPadding()[refIndex] != 0;
  }

}
