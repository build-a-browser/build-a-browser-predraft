package net.buildabrowser.babbrowser.browser.render.layout.imp;

import java.util.LinkedList;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;

public class SubStackingContextImp implements StackingContext {

  private final LinkedList<PosRefBoxFragment> fragments = new LinkedList<>();
  private final LinkedList<StackingContext> childContexts = new LinkedList<>();

  @Override
  public void defer(PosRefBoxFragment fragment) {
    assert fragment.referenceFragment() == null;
    fragments.add(fragment);
  }

  @Override
  public StackingContext start() {
    StackingContext childContext = new SubStackingContextImp();
    childContexts.add(childContext);

    return childContext;
  }

  @Override
  public void end(PosRefBoxFragment fragment) {
    assert fragment.referenceFragment() != null;
    fragments.add(fragment);
  }

  @Override
  public void layoutDeferred(CompositeLayer baseLayer) {
    int childContextIndex = 0;
    for (PosRefBoxFragment childFragment: fragments) {
      float[] insets = PositionUtil.computeInsets(
        childFragment,
        LayoutConstraint.of(baseLayer.width()),
        LayoutConstraint.of(baseLayer.height()));
      if (childFragment.referenceFragment() != null) {
        // Implicitly relative (or maybe sticky in the future, idk)
        CompositeLayer nextLayer = baseLayer.createChild(childFragment.referenceFragment());

        // layerX/layerY are based on content pos, but the layer will draw it's own borders
        float borderPaddingWidth = childFragment.contentX() - childFragment.borderX();
        float borderPaddingHeight = childFragment.contentY() - childFragment.borderY();
        nextLayer.incOffset(
          childFragment.layerX() - borderPaddingWidth + insets[2],
          childFragment.layerY() - borderPaddingHeight + insets[0]);

        // The child layer will activate "passthrough" mode, which redelegates it's child
        // layers to this layer (offset by nextLayer's position), preserving static positioning
        // details but having an effect as if there was no stacking context to affect the z-index. 
        childContexts.get(childContextIndex++).layoutDeferred(nextLayer);
      } else {
        StackingContext childContext = StackingContext.create();

        // TODO: This code currently assumes absolute, but it could be something else
        UnmanagedBoxFragment newFragment = PositionLayout.actuallyLayoutAbsolute(
          childFragment, childContext, insets, baseLayer);
        float[] offset = PositionLayout.positionAbsolute(insets, newFragment, baseLayer);

        CompositeLayer nextLayer = baseLayer.createChild(newFragment);
        nextLayer.incOffset(offset[0], offset[1]);
        //nextLayer.incOffset(offset[0], offset[1]);
        
        // This time, the child layer is used for both the deferred fragment and
        // it's stacking context
        childContext.layoutDeferred(nextLayer);
      }
    }
  }
  
}