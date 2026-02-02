package net.buildabrowser.babbrowser.browser.render.layout.imp;

import java.util.Deque;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;

public class StackingContextImp implements StackingContext {

  private final Deque<StackingContext> contextStack = new LinkedList<>();

  public StackingContextImp() {
    contextStack.push(new SubStackingContextImp());
  }

  @Override
  public void defer(PosRefBoxFragment fragment) {
    contextStack.peek().defer(fragment);
  }

  @Override
  public StackingContext start() {
    StackingContext childContext = contextStack.peek().start();
    contextStack.push(childContext);

    return this;
  }

  @Override
  public void end(PosRefBoxFragment fragment) {
    contextStack.pop();
    contextStack.peek().end(fragment);
  }

  @Override
  public void layoutDeferred(CompositeLayer baseLayer) {
    assert contextStack.size() == 1;
    contextStack.peek().layoutDeferred(baseLayer);
  }
  
}
