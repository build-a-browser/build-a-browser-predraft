package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayDeque;
import java.util.Deque;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class FlowBlockLayout {

  private final Deque<BlockFormattingContext> blockStack = new ArrayDeque<>();
  private final FlowRootContent rootContent;
  
  private BlockFormattingContext rootContext;

  public FlowBlockLayout(FlowRootContent rootContent) {
    this.rootContent = rootContent;
  }

  public void reset(ElementBox rootBox) {
    this.rootContext = new BlockFormattingContext(rootBox);
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
      if (childBox instanceof ElementBox elementBox && FlowUtil.isFloat(elementBox) && !isInInline) {
        UnmanagedBoxFragment floatFragment = FloatLayout.renderFloat(layoutContext, elementBox, widthConstraint, heightConstraint);
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
        inlineLayout.stageInline(childBox);
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
    FloatTracker floatTracker = rootContent.floatTracker();
    int floatMark = floatTracker.mark();

    ActiveStyles childStyles = childBox.activeStyles();
    // TODO: Factor in margins and stuff
    LayoutConstraint childWidthConstraint = evaluateNonReplacedBlockWidth(
      layoutContext, parentWidthConstraint, childStyles);
    LayoutConstraint childHeightConstraint = FlowHeightUtil.evaluateNonReplacedBlockLevelHeight(
      layoutContext, parentHeightConstraint, childStyles);

    BlockFormattingContext childContext = new BlockFormattingContext(childBox);
    blockStack.push(childContext);
    addChildrenToBlock(layoutContext, childBox, childWidthConstraint, childHeightConstraint);
    ManagedBoxFragment newFragment = childContext.close(childWidthConstraint, childHeightConstraint);
    blockStack.pop();

    BlockFormattingContext parentContext = blockStack.peek();
    newFragment.setPos(0, parentContext.currentY());

    parentContext.increaseY(newFragment.height());
    parentContext.minWidth(newFragment.width());
    parentContext.addFragment(newFragment);

    floatTracker.restoreMark(floatMark);
    floatTracker.adjustPos(0, newFragment.height());
  }

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
      evaluateNonReplacedBlockWidth(layoutContext, parentWidthConstraint, childStyles);
    
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

    BlockFormattingContext parentContext = blockStack.peek();
    newFragment.setPos(0, parentContext.currentY());

    parentContext.increaseY(height);
    parentContext.minWidth(width);
    parentContext.addFragment(newFragment);

    rootContent.floatTracker().adjustPos(0, height);
  }

  private LayoutConstraint evaluateNonReplacedBlockWidth(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ActiveStyles childStyles
  ) {
    LayoutConstraint determinedConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childStyles.getProperty(CSSProperty.WIDTH), childStyles);
    if (!determinedConstraint.type().equals(LayoutConstraintType.AUTO)) {
      return determinedConstraint;
    }

    return parentConstraint;
  }

}
