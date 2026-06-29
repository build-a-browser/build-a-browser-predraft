package net.buildabrowser.babbrowser.renderer.content.flow;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.isBlank;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.floats.ClearValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowBlockBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class FlowBlockLayout {

  private final FlowContext flowContext;

  private BlockFormattingContext rootContext;
  private BlockFormattingContext activeContext;

  public FlowBlockLayout(FlowContext context) {
    this.flowContext = context;
  }

  public void setup(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    this.rootContext = new BlockFormattingContext(
      rootBox, widthConstraint, heightConstraint, null,null);
    this.activeContext = rootContext;
  }

  public FlowBlockBoxFragment close(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    rootContext.collapse();
    return rootContext.close(
      widthConstraint, heightConstraint,
      flowContext.floatTracker().contentWidth(),
      flowContext.floatTracker().contentHeight());
  }

  public BlockFormattingContext activeContext() {
    return this.activeContext;
  }
  
  public void addChildrenToBlock(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FlowInlineLayout inlineLayout = flowContext.inlineLayout();
    PropertyContainer properties = box.properties();

    boolean isInInline = false;
    for (Box childBox: box.childBoxes()) {
      if (childBox instanceof ElementBox elementBox) {
        elementBox.content().computeMeasures(elementBox, widthConstraint);
      }
      if (
        childBox instanceof ElementBox elementBox
        && !PositionUtil.affectsLayout(elementBox)
      ) {
        if (isInInline) {
          inlineLayout.stageInline(box.layoutContext(), childBox);
        } else {
          addPositionedToBlock(elementBox);
        }
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
        UnmanagedBoxFragment<?> floatFragment = FloatLayout.renderFloat(
          elementBox, widthConstraint, heightConstraint);
        FloatLayout.addFloat(flowContext, floatFragment, widthConstraint, heightConstraint, 0);
        activeContext.addFragment(new FloatRefFragment(floatFragment));
      } else if (FlowUtil.isBlockLevel(childBox)) {
        if (isInInline) {
          inlineLayout.stopInline(widthConstraint, heightConstraint, properties);
          isInInline = false;
        }
        addToBlock((ElementBox) childBox, widthConstraint, heightConstraint);
      } else if (childBox instanceof TextBox textBox && isBlank(textBox.text())) {
        continue; // TODO: Check the actual spec-compliant way to handle this
      } else {
        activeContext.collapse();
        if (!isInInline) {
          inlineLayout.startInline(properties, widthConstraint);
          isInInline = true;
        }
        inlineLayout.stageInline(box.layoutContext(), childBox);
      }
    }

    if (isInInline) {
      inlineLayout.stopInline(widthConstraint, heightConstraint, properties);
    }
  }

  public void addToBlock(
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
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

    // TODO: Need to make sure this interacts properly with floats
    float alignStart = FlowAlignUtil.legacyAlign(
      parentContext, childBox, parentWidthConstraint, childWidthConstraint,
      0, parentWidthConstraint.value());

    float[] margin = childBox.dimensions().getComputedMargin();
    parentContext.recordMargin(margin[0]);
    boolean needsFloatClear = needsFloatClear(childBox);
    boolean collapseFirst = needsCollapsed(childBox, 0) || needsFloatClear;
    if (collapseFirst) {
      parentContext.collapse();
    }
    BlockFormattingContext collapseContext = collapseFirst ? null : parentContext;
    BlockFormattingContext childContext = new BlockFormattingContext(
      childBox, childWidthConstraint, childHeightConstraint, parentContext, collapseContext);

    ackFloatClear(childBox);
    activeContext = childContext;
    addChildrenToBlock(childBox, childWidthConstraint, childHeightConstraint);

    boolean collapseAfter = needsCollapsed(childBox, 1);
    if (collapseAfter) {
      childContext.collapse();
    }

    FlowBlockBoxFragment newFragment = childContext.close(childWidthConstraint, childHeightConstraint);
    activeContext = parentContext;
    
    addFinishedFragment(newFragment, alignStart, parentWidthConstraint);
    
    if (!collapseAfter) {
      parentContext.recordMargin(childContext.currentMaxMargin());
      parentContext.recordMargin(childContext.currentMinMargin());
    }
    parentContext.recordMargin(margin[1]);
  }

  // TODO: Clean this up some
  // TODO: I don't recall if this properly handles horizontal margins
  private void addUnmanagedBlockToBlock(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    FloatTracker floatTracker = flowContext.floatTracker();
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
      rightContent = parentWidthConstraint.value();
      childWidthConstraint = childBox.isReplaced() ?
        FlowWidthUtil.determineBlockReplacedWidthAndMargins(
          parentWidthConstraint, childBox) :
        FlowWidthUtil.evaluateNonReplacedBlockWidthAndMargins(
          parentWidthConstraint, childBox,
          leftContent, rightExtraMargin);
    }

    float alignStart = FlowAlignUtil.legacyAlign(
      activeContext, childBox, parentWidthConstraint,
      childWidthConstraint, leftContent, rightContent);

    float[] margin = childBox.dimensions().getComputedMargin();
    activeContext.recordMargin(Math.max(margin[0], minClear));
    activeContext.collapse();

    FragmentFactory fragmentFactory = childBox.layoutContext().global().fragmentFactory();
    UnmanagedBoxFragment<?> newFragment = parentWidthConstraint.isPreLayoutConstraint() ?
      fragmentFactory.createGenericUnmanagedBox(
        FlowUtil.constraintWidth(childBox, childWidthConstraint),
        FlowUtil.constraintHeight(childBox, childHeightConstraint),
        childBox) :
      childBox.layout(childWidthConstraint, childHeightConstraint);

    activeContext.recordMargin(margin[1]);

    addFinishedFragment(newFragment, Math.max(alignStart, leftContent), parentWidthConstraint);
  }

  public void addPositionedToBlock(ElementBox childBox) {
    BlockFormattingContext parentContext = activeContext;

    float estimatedAboveMargin = parentContext.currentMaxMargin() + parentContext.currentMinMargin();
    // Don't bother marking the float tracker position, the child should establish a new one

    LayoutFragment newFragment = PositionLayout.layout(childBox);
    parentContext.addFragment(newFragment); // Still needed to set fragment parent
    newFragment.setPos(0, estimatedAboveMargin + parentContext.currentY());
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
      // TODO: This might not work quite right with the new legacy align attributes
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
    CSSValue clearValue = elementBox.properties().get(CSSProperty.CLEAR);
    if (clearValue.equals(CSSValue.NONE)) return;
    float leftClear = clearValue.equals(ClearValue.RIGHT) ? 0 : flowContext.floatTracker().clearedLineStartPosition();
    float rightClear = clearValue.equals(ClearValue.LEFT) ? 0 : flowContext.floatTracker().clearedLineEndPosition();
    float totalClear = Math.max(leftClear, rightClear);
    activeContext.increaseY(totalClear, totalClear);
  }

  private boolean needsFloatClear(ElementBox elementBox) {
    CSSValue clearValue = elementBox.properties().get(CSSProperty.CLEAR);
    return !clearValue.equals(CSSValue.NONE);
  }

  private boolean needsCollapsed(ElementBox box, int refIndex) {
    ElementBoxDimensions dimensions = box.dimensions();
    return
      dimensions.getComputedBorder()[refIndex] != 0
      || dimensions.getComputedPadding()[refIndex] != 0;
  }

}
