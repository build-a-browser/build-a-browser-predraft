package net.buildabrowser.babbrowser.renderer.content.common.test;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;

public class TestUnmanagedBoxFragment extends UnmanagedBoxFragment<TestUnmanagedBoxFragment> {

  public TestUnmanagedBoxFragment(
    float x, float y,
    float width, float height,
    ElementBox box
  ) {
    super(
      width, height, width, height,
      0, 0,
      box, null);
    this.setPos(x, y);
  }

  @Override
  public BoxPainter<TestUnmanagedBoxFragment> painter() {
    throw new UnsupportedOperationException("Cannot access painter during testing!");
  }

  @Override
  protected EventHandler<TestUnmanagedBoxFragment> eventHandler() {
    throw new UnsupportedOperationException("Cannot access event handler during testing!");
  }

  @Override
  public TestUnmanagedBoxFragment newCopy() {
    throw new UnsupportedOperationException("Unimplemented method 'newCopy'");
  }
  
}
