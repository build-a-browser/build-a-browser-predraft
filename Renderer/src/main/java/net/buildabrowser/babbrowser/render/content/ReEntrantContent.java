package net.buildabrowser.babbrowser.render.content;

import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class ReEntrantContent implements BoxContent {

  private static final ReEntrantContent INSTANCE = new ReEntrantContent();

  private ReEntrantContent() {}

  @Override
  public UnmanagedBoxFragment layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    // BoxGeneratorImp tries to share content between the nodes to save on memory usage.
    // Sometimes, some nested nodes will all share the same FlowRootContent on first page load
    // Then, during a second layout cycle, one of the elements in the chain has a style change that causes it
    // to not be shared (e.g. a display, float, or position change).
    // For whatever reason, the children of that box aren't properly having their content updated, so the child box
    // now shares content with the ancestor when it should not. That content has a "pointer" (field referencing) the
    // ancestor box, so when the child tries to lay out, it starts instead laying out the ancestor again,
    // causing infinite recursion.

    throw new IllegalStateException(
      "Re-Entrant Layout Cycle Detected\n" +
      "This is a known bug, try reloading. See the comment in ReEntrantContent.java for more details");
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    throw new IllegalStateException("Reached unreachable code");
  }

  @Override
  public ElementBox rootBox() {
    throw new IllegalStateException("Reached unreachable code");
  }

  @Override
  public EventHandler eventHandler() {
    throw new IllegalStateException("Reached unreachable code");
  }

  public static ReEntrantContent instance() {
    return INSTANCE;
  }

}
