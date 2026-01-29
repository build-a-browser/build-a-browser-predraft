package net.buildabrowser.babbrowser.browser.render.layout.imp;

import java.util.ArrayDeque;
import java.util.Deque;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;

public class StackingContextImp implements StackingContext {

  private Deque<CompositeLayer> layers = new ArrayDeque<>();

  public StackingContextImp(int topWidth, int topHeight) {
    //layers.add(CompositeLayer.createRoot(topWidth, topHeight));
  }

  @Override
  public void defer(LayoutContext referenceContext, ElementBox box) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'defer'");
  }

  @Override
  public void add(PosRefBoxFragment fragment) {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'add'");
  }

  @Override
  public CompositeLayer layoutDeferred() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'layoutDeferred'");
  }
  
}