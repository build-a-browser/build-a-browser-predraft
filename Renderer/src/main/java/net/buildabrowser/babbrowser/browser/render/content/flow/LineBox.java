package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.LinkedList;
import java.util.Stack;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;

public class LineBox {

  private final Stack<LineSegment> lineSegments = new Stack<>();

  public LineBox() {
    lineSegments.add(new LineSegment(null, new LinkedList<>()));
  }

  private int totalWidth = 0;
  
  public void addFragment(FlowFragment flowFragment) {
    this.totalWidth += flowFragment.width();
    lineSegments.peek().fragments().add(flowFragment);
  }

  public void pushElement(ElementBox elementBox) {
    lineSegments.add(new LineSegment(elementBox, new LinkedList<>()));
  }

  public ElementBox popElement() {
    LineSegment lineSegment = lineSegments.pop();
    ManagedBoxFragment managedBoxFragment = new ManagedBoxFragment(
      lineSegment.width(), lineSegment.height(),
      lineSegment.box(), lineSegment.fragments());
    lineSegments.peek().fragments().add(managedBoxFragment);
    
    return managedBoxFragment.box();
  }

  public int totalWidth() {
    return this.totalWidth;
  }

  public LineBoxFragment toFragment() {
    LineSegment activeSegment = lineSegments.peek();
    return new LineBoxFragment(totalWidth, activeSegment.height(), activeSegment.fragments());
  }

}
