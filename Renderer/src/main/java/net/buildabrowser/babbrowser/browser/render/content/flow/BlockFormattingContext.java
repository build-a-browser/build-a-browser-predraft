package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;

public class BlockFormattingContext {

  private final ElementBox elementBox;

  private final List<FlowFragment> fragments;

  private int width;
  private int y;

  public BlockFormattingContext(ElementBox elementBox) {
    this.elementBox = elementBox;

    this.fragments = new ArrayList<>();
  }

  public int currentY() {
    return this.y;
  }

  public void increaseY(int yInc) {
    this.y += yInc;
  }

  public void minWidth(int minWidth) {
    this.width = Math.max(width, minWidth);
  }

  public void addFragment(FlowFragment newFragment) {
    this.fragments.add(newFragment);
  }

  public ManagedBoxFragment close(LayoutConstraint layoutConstraint) {
    if (layoutConstraint.type().equals(LayoutConstraintType.BOUNDED)) {
      return new ManagedBoxFragment(layoutConstraint.value(), y, elementBox, fragments);  
    }
    return new ManagedBoxFragment(width, y, elementBox, fragments);
  }

}