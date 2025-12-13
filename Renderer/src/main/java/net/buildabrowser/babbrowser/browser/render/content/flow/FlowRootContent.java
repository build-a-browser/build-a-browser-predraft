package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.List;
import java.util.Stack;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
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
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;

public class FlowRootContent implements BoxContent {

  private final ElementBox rootBox;
  private final Stack<BlockFormattingContext> blockStack;
  private final Stack<InlineFormattingContext> inlineStack;

  private ManagedBoxFragment rootFragment;

  public FlowRootContent(ElementBox box) {
    this.rootBox = box;
    this.blockStack = new Stack<>();
    this.inlineStack = new Stack<>();
  }

  @Override
  public void prelayout(LayoutContext layoutContext) {
    for (Box child: rootBox.childBoxes()) {
      if (child instanceof ElementBox elementBox) {
        elementBox.content().prelayout(layoutContext);
      }
    }

    ElementBoxDimensions dimensions = rootBox.dimensions();

    BlockFormattingContext rootContext = new BlockFormattingContext(rootBox);
    blockStack.clear();
    blockStack.add(rootContext);
    
    addChildrenToBlock(layoutContext, rootBox, LayoutConstraint.MIN_CONTENT);
    dimensions.setPreferredMinWidthConstraint(rootContext.close().width());

    blockStack.clear();
    blockStack.add(rootContext);

    addChildrenToBlock(layoutContext, rootBox, LayoutConstraint.MAX_CONTENT);
    dimensions.setPreferredWidthConstraint(rootContext.close().width());
  }

  @Override
  public void layout(LayoutContext layoutContext, LayoutConstraint layoutConstraint) {
    BlockFormattingContext rootContext = new BlockFormattingContext(rootBox);
    blockStack.clear();
    blockStack.add(rootContext);
    
    addChildrenToBlock(layoutContext, rootBox, layoutConstraint);

    this.rootFragment = rootContext.close();

    rootBox.dimensions().setComputedSize(rootFragment.width(), rootFragment.height());
  }

  @Override
  public void paint(PaintCanvas canvas) {
    FlowRootContentPainter.paintFragment(canvas, rootFragment);
  }

  private void addChildrenToBlock(LayoutContext layoutContext, ElementBox box, LayoutConstraint layoutConstraint) {
    boolean isInInline = false;
    for (Box childBox: box.childBoxes()) {
      if (isBlockLevel(childBox)) {
        if (isInInline) {
          stopInline();
          isInInline = false;
        }
        addToBlock(layoutContext, (ElementBox) childBox, layoutConstraint);
      } else {
        if (!isInInline) {
          startInline();
          isInInline = true;
        }
        addToInline(layoutContext, childBox, layoutConstraint);
      }
    }

    if (isInInline) {
      stopInline();
    }
  }

  private void stopInline() {
    InlineFormattingContext formattingContext = inlineStack.pop();
    BlockFormattingContext blockContext = blockStack.peek();
    for (LineBoxFragment fragment: formattingContext.fragments()) {
      positionFragment(fragment, blockContext.currentY());
      blockContext.addFragment(fragment);
      blockContext.minWidth(fragment.width());
      blockContext.increaseY(fragment.height());
    }
  }

  private void startInline() {
    inlineStack.push(new InlineFormattingContext());
  }

  private boolean isBlockLevel(Box childBox) {
    return switch(childBox) {
      case ElementBox elementBox -> elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL);
      case TextBox _ -> false;
      default -> throw new UnsupportedOperationException("Unknown box type!");
    };
  }

  private void addToBlock(LayoutContext layoutContext, ElementBox elementBox, LayoutConstraint layoutConstraint) {
    if (isInFlow(elementBox)) {
      addManagedBlockToBlock(layoutContext, elementBox, layoutConstraint);
    } else {
      addUnmanagedBlockToBlock(layoutContext, elementBox, layoutConstraint);
    }
  }

  private void addManagedBlockToBlock(LayoutContext layoutContext, ElementBox childBox, LayoutConstraint layoutConstraint) {
    BlockFormattingContext childContext = new BlockFormattingContext(childBox);
    blockStack.push(childContext);
    addChildrenToBlock(layoutContext, childBox, layoutConstraint);
    ManagedBoxFragment newFragment = childContext.close();
    blockStack.pop();

    BlockFormattingContext parentContext = blockStack.peek();
    newFragment.setPos(0, parentContext.currentY());

    parentContext.increaseY(newFragment.height());
    parentContext.minWidth(newFragment.width());
    parentContext.addFragment(newFragment);
  }

  private void addUnmanagedBlockToBlock(LayoutContext layoutContext, ElementBox elementBox, LayoutConstraint layoutConstraint) {
    if (!layoutConstraint.isPreLayoutConstraint()) {
      elementBox.content().layout(layoutContext, layoutConstraint);
    }
    int width = constraintWidth(elementBox.dimensions(), layoutConstraint);
    int height = elementBox.dimensions().getComputedHeight();

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(width, height, elementBox);

    BlockFormattingContext parentContext = blockStack.peek();
    newFragment.setPos(0, parentContext.currentY());

    parentContext.increaseY(height);
    parentContext.minWidth(width);
    parentContext.addFragment(newFragment);
  }


  private void addToInline(LayoutContext layoutContext, Box childBox, LayoutConstraint layoutConstraint) {
    switch (childBox) {
      case ElementBox elementBox -> addToInline(layoutContext, elementBox, layoutConstraint);
      case TextBox textBox -> addTextToInline(layoutContext, textBox);
      default -> throw new UnsupportedOperationException("Unknown box type!");
    }
  }

  private void addToInline(LayoutContext layoutContext, ElementBox elementBox, LayoutConstraint layoutConstraint) {
    if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
      InlineFormattingContext nextContext = inlineStack.peek().split();
      stopInline();
      addToBlock(layoutContext, elementBox, layoutConstraint);
      inlineStack.push(nextContext);
    } else if (isInFlow(elementBox)) {
      addManagedBlockToInline(layoutContext, elementBox, layoutConstraint);
    } else {
      addUnmanagedBlockToInline(layoutContext, elementBox, layoutConstraint);
    }
  }

  private void addManagedBlockToInline(LayoutContext layoutContext, ElementBox elementBox, LayoutConstraint layoutConstraint) {
    inlineStack.peek().pushElement(elementBox);
    for (Box childBox: elementBox.childBoxes()) {
      addToInline(layoutContext, childBox, layoutConstraint);
    }
    inlineStack.peek().popElement();
  }

  private void addUnmanagedBlockToInline(LayoutContext layoutContext, ElementBox elementBox, LayoutConstraint layoutConstraint) {
    if (!layoutConstraint.isPreLayoutConstraint()) {
      elementBox.content().layout(layoutContext, layoutConstraint);
    }
    int width = elementBox.dimensions().getComputedWidth();
    int height = elementBox.dimensions().getComputedHeight();

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(width, height, elementBox);

    InlineFormattingContext parentContext = inlineStack.peek();
    parentContext.addFragment(newFragment);
  }

  private void addTextToInline(LayoutContext layoutContext, TextBox textBox) {
    FontMetrics fontMetrics = layoutContext.fontMetrics();
    String text = textBox.text().data().trim();
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

  private int constraintWidth(ElementBoxDimensions dimensions, LayoutConstraint layoutConstraint) {
    return switch (layoutConstraint.type()) {
      case BOUNDED -> layoutConstraint.value();
      case AUTO -> dimensions.getComputedWidth();
      case MIN_CONTENT -> dimensions.preferredMinWidthConstraint();
      case MAX_CONTENT -> dimensions.preferredWidthConstraint();
      default -> throw new UnsupportedOperationException("Unsupported constraint type!");
    };
  }

  private boolean isInFlow(ElementBox elementBox) {
    return
      elementBox.activeStyles().innerDisplayValue().equals(InnerDisplayValue.FLOW)
      && !elementBox.isReplaced();
  }

}
