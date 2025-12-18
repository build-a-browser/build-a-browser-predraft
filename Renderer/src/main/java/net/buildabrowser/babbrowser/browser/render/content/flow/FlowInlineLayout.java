package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.List;
import java.util.Stack;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles.SizingUnit;

public class FlowInlineLayout {

  private final Stack<InlineFormattingContext> inlineStack = new Stack<>();
  private final FlowRootContent rootContent;

  public FlowInlineLayout(FlowRootContent rootContent) {
    this.rootContent = rootContent;
  }

  public void stopInline() {
    InlineFormattingContext formattingContext = inlineStack.pop();
    BlockFormattingContext blockContext = rootContent.blockLayout().activeContext();
    for (LineBoxFragment fragment: formattingContext.fragments()) {
      positionFragment(fragment, blockContext.currentY());
      blockContext.addFragment(fragment);
      blockContext.minWidth(fragment.width());
      blockContext.increaseY(fragment.height());
    }
  }

  public void startInline() {
    inlineStack.push(new InlineFormattingContext());
  }

  public void addToInline(
    LayoutContext layoutContext,
    Box childBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    switch (childBox) {
      case ElementBox elementBox -> addToInline(
        layoutContext, elementBox, widthConstraint, heightConstraint);
      case TextBox textBox -> addTextToInline(layoutContext, textBox);
      default -> throw new UnsupportedOperationException("Unknown box type!");
    }
  }

  private void addToInline(
    LayoutContext layoutContext,
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
      InlineFormattingContext nextContext = inlineStack.peek().split();
      stopInline();
      rootContent.blockLayout().addToBlock(
        layoutContext, elementBox, widthConstraint, heightConstraint);
      inlineStack.push(nextContext);
    } else if (FlowUtil.isInFlow(elementBox)) {
      addManagedBlockToInline(layoutContext, elementBox, widthConstraint, heightConstraint);
    } else {
      addUnmanagedBlockToInline(layoutContext, elementBox, widthConstraint, heightConstraint);
    }
  }

  private void addManagedBlockToInline(
    LayoutContext layoutContext,
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    inlineStack.peek().pushElement(elementBox);
    for (Box childBox: elementBox.childBoxes()) {
      addToInline(layoutContext, childBox, widthConstraint, heightConstraint);
    }
    inlineStack.peek().popElement();
  }

  private void addUnmanagedBlockToInline(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ActiveStyles childStyles = childBox.activeStyles();
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      determineInlineBlockReplacedWidth(
        layoutContext, parentWidthConstraint, childStyles, childBox.dimensions()) :
      determineInlineBlockNonReplacedWidth(
        layoutContext, parentWidthConstraint, childStyles, childBox.dimensions());
    
    if (!parentWidthConstraint.isPreLayoutConstraint()) {
      childBox.content().layout(layoutContext, childWidthConstraint, heightConstraint);
    }

    int width = LayoutUtil.constraintOrDim(childWidthConstraint, childBox.dimensions().getComputedWidth());
    int height = childBox.dimensions().getComputedHeight();

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(width, height, childBox);

    InlineFormattingContext parentContext = inlineStack.peek();
    parentContext.addFragment(newFragment);
  }

  private void addTextToInline(LayoutContext layoutContext, TextBox textBox) {
    FontMetrics fontMetrics = layoutContext.fontMetrics();
    String text = textBox.text().trim();
    if (text.isBlank()) return;

    int width = fontMetrics.stringWidth(text);
    int height = fontMetrics.fontHeight();
    TextFragment newFragment = new TextFragment(width, height, text);

    InlineFormattingContext parentContext = inlineStack.peek();
    parentContext.addFragment(newFragment);
  }
  
  private void positionFragment(LineBoxFragment fragment, int y) {
    fragment.setPos(0, y);
    positionFragmentElements(fragment.fragments());
  }

  private void positionFragmentElements(List<FlowFragment> fragments) {
    int x = 0;
    for (FlowFragment child: fragments) {
      child.setPos(x, 0);
      x += child.width();
      if (child instanceof ManagedBoxFragment managedBoxFragment) {
        positionFragmentElements(managedBoxFragment.fragments());
      }
    }
  }

  private LayoutConstraint determineInlineBlockNonReplacedWidth(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ActiveStyles childStyles,
    ElementBoxDimensions boxDimensions
  ) {
    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childStyles.getSizingProperty(SizingUnit.WIDTH), childStyles);
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    int preferredMinWidth = boxDimensions.preferredMinWidthConstraint();
    int preferredWidth = boxDimensions.preferredWidthConstraint();
    int availableWidth = parentConstraint.value();
    if (!parentConstraint.type().equals(LayoutConstraintType.BOUNDED)) {
      return LayoutConstraint.of(preferredWidth);
    }

    return LayoutConstraint.of(Math.min(Math.max(preferredMinWidth, availableWidth), preferredWidth));
  }

  private LayoutConstraint determineInlineBlockReplacedWidth(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ActiveStyles childStyles,
    ElementBoxDimensions boxDimensions
  ) {
    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childStyles.getSizingProperty(SizingUnit.WIDTH), childStyles);
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    if (
      boxDimensions.intrinsicWidth() != -1
      && boxDimensions.getComputedHeight() != -1
    ) {
      return LayoutConstraint.of(boxDimensions.intrinsicWidth());
    } else if (
      boxDimensions.intrinsicRatio() != -1
      && boxDimensions.intrinsicHeight() != -1
    ) { // TODO: Also consider specified height
      int usedHeight = boxDimensions.intrinsicHeight();
      int usedWidth = (int) (usedHeight * boxDimensions.intrinsicRatio());
      return LayoutConstraint.of(usedWidth);
    } else if (boxDimensions.intrinsicRatio() != -1) {
      // TODO: Compute as for block non-replaced
      return LayoutConstraint.of(boxDimensions.preferredWidthConstraint());
    } else if (boxDimensions.intrinsicWidth() != -1) {
      return LayoutConstraint.of(boxDimensions.intrinsicWidth());
    } else {
      // TODO: Check if window smaller than 300px
      return LayoutConstraint.of(300);
    }
  }

}
