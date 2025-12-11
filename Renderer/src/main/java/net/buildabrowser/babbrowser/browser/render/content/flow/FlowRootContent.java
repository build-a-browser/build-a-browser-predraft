package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.List;
import java.util.Stack;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
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
  public void layout(LayoutContext layoutContext) {
    BlockFormattingContext rootContext = new BlockFormattingContext(rootBox);
    blockStack.clear();
    blockStack.add(rootContext);
    
    addChildrenToBlock(layoutContext, rootBox);

    this.rootFragment = rootContext.close();

    rootBox.dimensions().setComputedSize(rootFragment.width(), rootFragment.height());
  }

  @Override
  public void paint(PaintCanvas canvas) {
    FlowRootContentPainter.paintFragment(canvas, rootFragment);
  }

  private void addChildrenToBlock(LayoutContext layoutContext, ElementBox box) {
    boolean isInInline = false;
    for (Box childBox: box.childBoxes()) {
      if (isBlockLevel(childBox)) {
        if (isInInline) {
          stopInline();
          isInInline = false;
        }
        addToBlock(layoutContext, (ElementBox) childBox);
      } else {
        if (!isInInline) {
          startInline();
          isInInline = true;
        }
        addToInline(layoutContext, childBox);
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

  private void addToBlock(LayoutContext layoutContext, ElementBox elementBox) {
    if (isInFlow(elementBox)) {
      addManagedBlockToBlock(layoutContext, elementBox);
    } else {
      addUnmanagedBlockToBlock(layoutContext, elementBox);
    }
  }

  private void addManagedBlockToBlock(LayoutContext layoutContext, ElementBox childBox) {
    BlockFormattingContext childContext = new BlockFormattingContext(childBox);
    blockStack.push(childContext);
    addChildrenToBlock(layoutContext, childBox);
    ManagedBoxFragment newFragment = childContext.close();
    blockStack.pop();

    BlockFormattingContext parentContext = blockStack.peek();
    newFragment.setPos(0, parentContext.currentY());

    parentContext.increaseY(newFragment.height());
    parentContext.minWidth(newFragment.width());
    parentContext.addFragment(newFragment);
  }

  private void addUnmanagedBlockToBlock(LayoutContext layoutContext, ElementBox elementBox) {
    elementBox.content().layout(layoutContext);
    int width = elementBox.dimensions().getComputedWidth();
    int height = elementBox.dimensions().getComputedHeight();

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(width, height, elementBox);

    BlockFormattingContext parentContext = blockStack.peek();
    newFragment.setPos(0, parentContext.currentY());

    parentContext.increaseY(height);
    parentContext.minWidth(width);
    parentContext.addFragment(newFragment);
  }

  private boolean isInFlow(ElementBox elementBox) {
    return
      elementBox.activeStyles().innerDisplayValue().equals(InnerDisplayValue.FLOW)
      && !elementBox.isReplaced();
  }


  private void addToInline(LayoutContext layoutContext, Box childBox) {
    switch (childBox) {
      case ElementBox elementBox -> addToInline(layoutContext, elementBox);
      case TextBox textBox -> addTextToInline(layoutContext, textBox);
      default -> throw new UnsupportedOperationException("Unknown box type!");
    }
  }

  private void addToInline(LayoutContext layoutContext, ElementBox elementBox) {
    if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
      // TODO: Handle block splitting an inline
    } else if (isInFlow(elementBox)) {
      addManagedBlockToInline(layoutContext, elementBox);
    } else {
      addUnmanagedBlockToInline(layoutContext, elementBox);
    }
  }

  private void addManagedBlockToInline(LayoutContext layoutContext, ElementBox elementBox) {
    InlineFormattingContext parentContext = inlineStack.peek();
    parentContext.pushElement(elementBox);
    for (Box childBox: elementBox.childBoxes()) {
      addToInline(layoutContext, childBox);
    }
    parentContext.popElement();
  }

  private void addUnmanagedBlockToInline(LayoutContext layoutContext, ElementBox elementBox) {
    elementBox.content().layout(layoutContext);
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

}
