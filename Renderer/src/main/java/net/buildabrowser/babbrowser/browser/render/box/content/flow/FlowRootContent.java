package net.buildabrowser.babbrowser.browser.render.box.content.flow;

import java.util.Stack;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.content.flow.FlowFragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.box.content.flow.FlowFragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.box.content.flow.FlowFragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;

public class FlowRootContent implements BoxContent {

  private final ElementBox box;
  private final Stack<BlockFormattingContext> blockStack;

  private ManagedBoxFragment rootFragment;

  public FlowRootContent(ElementBox box) {
    this.box = box;
    this.blockStack = new Stack<>();
  }

  @Override
  public void layout(LayoutContext layoutContext) {
    int initX = box.dimensions().getLayoutX();
    int initY = box.dimensions().getLayoutY();
    BlockFormattingContext rootContext = new BlockFormattingContext(initX, initY, box);
    blockStack.clear();
    blockStack.add(rootContext);
    
    for (Box childBox: box.childBoxes()) {
      addToBlock(layoutContext, childBox);
    }

    this.rootFragment = rootContext.close();

    box.dimensions().setComputedSize(rootFragment.width(), rootFragment.height());
  }

  @Override
  public void paint(PaintCanvas canvas) {
    paintFragment(rootFragment, canvas);
  }

  private void addToBlock(LayoutContext layoutContext, Box childBox) {
    switch (childBox) {
      case ElementBox elementBox:
        if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
          if (isInFlow(elementBox)) {
            addManagedBlockToBlock(layoutContext, elementBox);
          } else {
            addUnmanagedBlockToBlock(layoutContext, elementBox);
          }
        }
        break;
      default:
        // TODO: Support text
        break;
    }
  }

  private void addManagedBlockToBlock(LayoutContext layoutContext, ElementBox childBox) {
    BlockFormattingContext parentContext = blockStack.peek();
    int initX = parentContext.currentX();
    int initY = parentContext.currentY();

    BlockFormattingContext childContext = new BlockFormattingContext(initX, initY, childBox);
    blockStack.push(childContext);
    for (Box childChildBox: childBox.childBoxes()) {
      addToBlock(layoutContext, childChildBox);
    }
    ManagedBoxFragment newFragment = childContext.close();
    blockStack.pop();

    parentContext.increaseY(newFragment.height());
    parentContext.minWidth(newFragment.width());
    parentContext.addFragment(newFragment);
  }

  private void addUnmanagedBlockToBlock(LayoutContext layoutContext, ElementBox elementBox) {
    BlockFormattingContext parentContext = blockStack.peek();
    int initX = parentContext.currentX();
    int initY = parentContext.currentY();
    
    elementBox.dimensions().setLayoutPos(initX, initY);
    elementBox.content().layout(layoutContext);
    int height = elementBox.dimensions().getComputedHeight();
    int width = elementBox.dimensions().getComputedWidth();

    UnmanagedBoxFragment newFragment = new UnmanagedBoxFragment(initX, initY, elementBox);

    parentContext.increaseY(height);
    parentContext.minWidth(width);
    parentContext.addFragment(newFragment);
  }

  private boolean isInFlow(ElementBox elementBox) {
    return
      elementBox.activeStyles().innerDisplayValue().equals(InnerDisplayValue.FLOW)
      && !elementBox.isReplaced();
  }

  private void paintFragment(FlowFragment fragment, PaintCanvas canvas) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> paintManagedBoxFragement(boxFragment, canvas);
      case UnmanagedBoxFragment boxFragment -> boxFragment.box().content().paint(canvas);
      case TextFragment textFragment -> paintTextFragment(textFragment, canvas);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!");
    }
  }

  private void paintManagedBoxFragement(ManagedBoxFragment fragment, PaintCanvas canvas) {
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().backgroundColor()));
    canvas.drawBox(fragment.x(), fragment.y(), fragment.width(), fragment.height());
    canvas.alterPaint(paint -> paint.setColor(box.activeStyles().textColor()));
    for (FlowFragment childFragment: fragment.fragments()) {
      canvas.pushPaint();
      paintFragment(childFragment, canvas);
      canvas.popPaint();
    }
  }

  private void paintTextFragment(TextFragment textFragment, PaintCanvas canvas) {
    
  }

}
