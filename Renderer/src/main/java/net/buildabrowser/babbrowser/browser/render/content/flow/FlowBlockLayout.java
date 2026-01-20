package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayDeque;
import java.util.Deque;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
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

  public void reset(ElementBox rootBox, LayoutConstraint widthConstraint) {
    this.rootContext = new BlockFormattingContext(rootBox, widthConstraint);
    blockStack.clear();
    blockStack.add(rootContext);
  }

  public ManagedBoxFragment close(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
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
        FlowBorderUtil.computeBorder(layoutContext, elementBox, activeContext());
        FlowPaddingUtil.computePadding(layoutContext, elementBox, activeContext());
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
    int[] border = childBox.dimensions().getComputedBorder();
    int[] padding = childBox.dimensions().getComputedPadding();
    FloatTracker floatTracker = rootContent.floatTracker();
    int floatMark = floatTracker.mark();
    floatTracker.adjustPos(border[2] + padding[2], border[0] + padding[0]);

    ActiveStyles childStyles = childBox.activeStyles();
    // TODO: Factor in margins and stuff
    LayoutConstraint childWidthConstraint = evaluateNonReplacedBlockWidth(
      layoutContext, parentWidthConstraint, childBox);
    LayoutConstraint childHeightConstraint = FlowHeightUtil.evaluateNonReplacedBlockLevelHeight(
      layoutContext, parentHeightConstraint, childStyles);

    BlockFormattingContext childContext = new BlockFormattingContext(childBox, childWidthConstraint);
    blockStack.push(childContext);
    addChildrenToBlock(layoutContext, childBox, childWidthConstraint, childHeightConstraint);
    ManagedBoxFragment newFragment = childContext.close(childWidthConstraint, childHeightConstraint);
    blockStack.pop();
    floatTracker.adjustPos(-border[2] - padding[2], 0); // TODO: Include in the mark?
    floatTracker.restoreMark(floatMark);

    addFinishedFragment(newFragment, 0);
  }

  // TODO: Handle the edge case where an unmanaged block interacts with a float
  // TODO: This method is growing very unwieldy...
  private void addUnmanagedBlockToBlock(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    ActiveStyles childStyles = childBox.activeStyles();
    // TODO: Account for stuff likes margins
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidth(
        layoutContext, parentWidthConstraint, childStyles, childBox.dimensions()) :
      evaluateNonReplacedBlockWidth(layoutContext, parentWidthConstraint, childBox);
    
    LayoutConstraint childHeightConstraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeight(
        layoutContext, parentHeightConstraint, childWidthConstraint,
        childStyles, childBox.dimensions()) :
      FlowHeightUtil.evaluateNonReplacedBlockLevelHeight(layoutContext, parentHeightConstraint, childStyles);

    if (!parentWidthConstraint.isPreLayoutConstraint()) {
      childBox.content().layout(layoutContext, childWidthConstraint, childHeightConstraint);
    }

    int width = FlowUtil.constraintWidth(childBox.dimensions(), childWidthConstraint);
    int height = FlowUtil.constraintHeight(childBox.dimensions(), childHeightConstraint);

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(width, height, childBox);
    addFinishedFragment(newFragment, 0);
  }

  public void addFinishedFragment(FlowFragment newFragment, int posX) {
    BlockFormattingContext parentContext = activeContext();
    newFragment.setPos(posX, parentContext.currentY());

    parentContext.increaseY(newFragment.borderHeight());
    parentContext.minWidth(newFragment.borderWidth());
    parentContext.addFragment(newFragment);

    rootContent.floatTracker().adjustPos(0, newFragment.borderHeight());
  }

  private LayoutConstraint evaluateNonReplacedBlockWidth(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    LayoutConstraint determinedConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childBox.activeStyles().getProperty(CSSProperty.WIDTH));
    if (!determinedConstraint.type().equals(LayoutConstraintType.AUTO)) {
      return determinedConstraint;
    }
    if (parentConstraint.type().equals(LayoutConstraintType.BOUNDED)) {
      int[] border = childBox.dimensions().getComputedBorder();
      int[] padding = childBox.dimensions().getComputedPadding();
      int adjustedWidth = parentConstraint.value() - border[2] - border[3] - padding[2] - padding[3];
      return LayoutConstraint.of(adjustedWidth);
    }

    return parentConstraint;
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

}
