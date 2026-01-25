package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayDeque;
import java.util.Deque;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.content.common.BorderUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.PositionUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class FlowBlockLayout {

  private final Deque<BlockFormattingContext> blockStack = new ArrayDeque<>();
  private final FlowRootContent rootContent;
  
  private BlockFormattingContext rootContext;

  public FlowBlockLayout(FlowRootContent rootContent) {
    this.rootContent = rootContent;
  }

  public void reset(ElementBox rootBox, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    this.rootContext = new BlockFormattingContext(rootBox,
      widthConstraint, heightConstraint, rootContent, null);
    blockStack.clear();
    blockStack.add(rootContext);
  }

  public ManagedBoxFragment close(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    rootContext.collapse();
    return rootContext.close(widthConstraint, heightConstraint);
  }

  public BlockFormattingContext activeContext() {
    return blockStack.peek();
  }
  
  public void addChildrenToBlock(
    LayoutContext layoutContext,
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FlowInlineLayout inlineLayout = rootContent.inlineLayout();
    ActiveStyles boxStyles = box.activeStyles();

    boolean isInInline = false;
    for (Box childBox: box.childBoxes()) {
      if (childBox instanceof ElementBox elementBox) {
        BorderUtil.computeBorder(layoutContext, elementBox, widthConstraint);
        PaddingUtil.computePadding(layoutContext, elementBox, widthConstraint);
        PositionUtil.computeInsets(layoutContext, elementBox, widthConstraint, heightConstraint);
      }
      if (childBox instanceof ElementBox elementBox && FlowUtil.isFloat(elementBox) && !isInInline) {
        ackFloatClear(elementBox);
        UnmanagedBoxFragment floatFragment = FloatLayout.renderFloat(
          layoutContext, elementBox, widthConstraint, heightConstraint);
        FloatLayout.addFloat(rootContent, floatFragment, widthConstraint, heightConstraint, 0);
      } else if (FlowUtil.isBlockLevel(childBox)) {
        if (isInInline) {
          inlineLayout.stopInline(layoutContext, widthConstraint, heightConstraint, boxStyles);
          isInInline = false;
        }
        addToBlock(layoutContext, (ElementBox) childBox, widthConstraint, heightConstraint);
      } else if (childBox instanceof TextBox textBox && textBox.text().isBlank()) {
        continue; // TODO: Check the actual spec-compliant way to handle this
      } else {
        activeContext().collapse();
        if (!isInInline) {
          inlineLayout.startInline(boxStyles, widthConstraint);
          isInInline = true;
        }
        inlineLayout.stageInline(layoutContext, childBox);
      }
    }

    if (isInInline) {
      inlineLayout.stopInline(layoutContext, widthConstraint, heightConstraint, boxStyles);
    }
  }

  public void addToBlock(
    LayoutContext layoutContext,
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ackFloatClear(elementBox);
    if (FlowUtil.isInFlow(elementBox)) {
      addManagedBlockToBlock(layoutContext, elementBox, widthConstraint, heightConstraint);
    } else {
      addUnmanagedBlockToBlock(layoutContext, elementBox, widthConstraint, heightConstraint);
    }
  }

  private void addManagedBlockToBlock(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    BlockFormattingContext parentContext = activeContext();
    LayoutConstraint childWidthConstraint = FlowWidthUtil.evaluateNonReplacedBlockWidthAndMargins(
      layoutContext, parentWidthConstraint, childBox);
    LayoutConstraint childHeightConstraint = FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
      layoutContext, parentHeightConstraint, parentWidthConstraint, childBox);

    int[] margin = childBox.dimensions().getComputedMargin();
    int[] border = childBox.dimensions().getComputedBorder();
    int[] padding = childBox.dimensions().getComputedPadding();
    FloatTracker floatTracker = rootContent.floatTracker();
    int floatMark = floatTracker.mark();
    floatTracker.adjustPos(margin[2] + border[2] + padding[2], border[0] + padding[0]);
    int preMargin = parentContext.currentY();

    parentContext.recordMargin(margin[0]);
    boolean collapseFirst = needsCollapsed(childBox, 0);
    if (collapseFirst) {
      parentContext.collapse();
    }
    BlockFormattingContext collapseContext = collapseFirst ? null : parentContext;
    BlockFormattingContext childContext = new BlockFormattingContext(childBox,
      childWidthConstraint, childHeightConstraint, rootContent, collapseContext);
    blockStack.push(childContext);

    addChildrenToBlock(layoutContext, childBox, childWidthConstraint, childHeightConstraint);

    boolean collapseAfter = needsCollapsed(childBox, 1);
    if (collapseAfter) {
      childContext.collapse();
    }

    ManagedBoxFragment newFragment = childContext.close(childWidthConstraint, childHeightConstraint);
    blockStack.pop();

    floatTracker.restoreMark(floatMark); // TODO: Ensure we still account for collapsed padding
    floatTracker.adjustPos(-margin[2] - border[2] - padding[2], activeContext().currentY() - preMargin); // TODO: Include X in the mark?

    addFinishedFragment(newFragment, margin[2]);
    
    if (!collapseAfter) {
      parentContext.recordMargin(childContext.currentMaxMargin());
      parentContext.recordMargin(childContext.currentMinMargin());
    }
    parentContext.recordMargin(margin[1]);
  }

  // TODO: Handle the edge case where an unmanaged block interacts with a float
  private void addUnmanagedBlockToBlock(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        layoutContext, parentWidthConstraint, childBox) :
      FlowWidthUtil.evaluateNonReplacedBlockWidthAndMargins(
        layoutContext, parentWidthConstraint, childBox);
    LayoutConstraint childHeightConstraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        layoutContext, parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        layoutContext, parentHeightConstraint, parentWidthConstraint, childBox);

    int[] margin = childBox.dimensions().getComputedMargin();

    activeContext().recordMargin(margin[0]);
    activeContext().collapse();
    if (!parentWidthConstraint.isPreLayoutConstraint()) {
      childBox.content().layout(layoutContext, childWidthConstraint, childHeightConstraint);
    }
    activeContext().recordMargin(margin[1]);

    int width = FlowUtil.constraintWidth(childBox.dimensions(), childWidthConstraint);
    int height = FlowUtil.constraintHeight(childBox.dimensions(), childHeightConstraint);

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(width, height, childBox);
    addFinishedFragment(newFragment, margin[2]);
  }

  public void addFinishedFragment(LayoutFragment newFragment, int posX) {
    BlockFormattingContext parentContext = activeContext();
    newFragment.setPos(posX, parentContext.currentY());

    parentContext.increaseY(newFragment.borderHeight());
    parentContext.minWidth(newFragment.marginX() + newFragment.marginWidth());
    parentContext.addFragment(newFragment);

    rootContent.floatTracker().adjustPos(0, newFragment.borderHeight());
  }

  private void ackFloatClear(ElementBox elementBox) {
    CSSValue clearValue = elementBox.activeStyles().getProperty(CSSProperty.CLEAR);
    if (clearValue.equals(CSSValue.NONE)) return;
    int leftClear = clearValue.equals(ClearValue.RIGHT) ? 0 : rootContent.floatTracker().clearedLineStartPosition();
    int rightClear = clearValue.equals(ClearValue.LEFT) ? 0 : rootContent.floatTracker().clearedLineEndPosition();
    int totalClear = Math.max(leftClear, rightClear);
    blockStack.peek().increaseY(totalClear);
    rootContent.floatTracker().adjustPos(0, totalClear);
  }

  private boolean needsCollapsed(ElementBox box, int refIndex) {
    ElementBoxDimensions dimensions = box.dimensions();
    return
      dimensions.getComputedBorder()[refIndex] != 0
      || dimensions.getComputedPadding()[refIndex] != 0;
  }

}
