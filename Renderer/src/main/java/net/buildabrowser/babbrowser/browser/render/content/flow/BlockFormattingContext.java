package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;

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

  public ManagedBoxFragment close(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    int usedWidth = LayoutUtil.constraintOrDim(widthConstraint, width);
    int usedHeight = LayoutUtil.constraintOrDim(heightConstraint, y);
    return new ManagedBoxFragment(usedWidth, usedHeight, elementBox, fragments);
  }

}