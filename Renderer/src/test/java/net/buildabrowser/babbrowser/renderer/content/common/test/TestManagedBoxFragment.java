package net.buildabrowser.babbrowser.renderer.content.common.test;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;

public class TestManagedBoxFragment extends ManagedBoxFragment<TestManagedBoxFragment> {

  public TestManagedBoxFragment(
    float x, float y,
    float width, float height,
    ElementBox box, List<LayoutFragment> fragments
  ) {
    super(
      width, height, width, height, box,
      IntrusiveList.fromList(fragments));
    this.setPos(x, y);
  }

  @Override
  public BoxPainter<TestManagedBoxFragment> painter() {
    throw new UnsupportedOperationException("Cannot access painter during testing!");
  }

  @Override
  protected EventHandler<TestManagedBoxFragment> eventHandler() {
    throw new UnsupportedOperationException("Cannot access event handler during testing!");
  }
  
}
