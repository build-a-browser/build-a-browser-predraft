package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;

public class LineBox {

  private final Deque<LineSegment> lineSegments;

  public LineBox() {
    this.lineSegments = new ArrayDeque<>();
    lineSegments.push(new LineSegment(null, new LinkedList<>()));
  }

  private LineBox(ArrayDeque<LineSegment> segments) {
    this.lineSegments = segments;
  }

  private int totalWidth = 0;
  
  public void addFragment(FlowFragment flowFragment) {
    // TODO: Account for padding
    this.totalWidth += flowFragment.contentWidth();
    lineSegments.peek().fragments().add(flowFragment);
  }

  public void appendText(String text, int width, int height) {
    // TODO: Merge fragments to reduce memory
    addFragment(new TextFragment(width, height, text));
  }

  public void pushElement(ElementBox elementBox) {
    lineSegments.push(new LineSegment(elementBox, new LinkedList<>()));
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

  // TODO: This is surely wrong...
  public int totalHeight() {
    int totalHeight = 0;
    for (LineSegment segment: this.lineSegments) {
      totalHeight = Math.max(totalHeight, segment.height());
    }

    return totalHeight;
  }

  public LineBoxFragment toFragment() {
    LineSegment activeSegment = lineSegments.peek();
    return new LineBoxFragment(totalWidth, activeSegment.height(), activeSegment.fragments());
  }

  public LineBox split() {
    ArrayDeque<LineSegment> newSegments = new ArrayDeque<>();
    Iterator<LineSegment> it = lineSegments.descendingIterator();
    while (it.hasNext()) {
      LineSegment oldSegment = it.next();
      LineSegment newSegment = new LineSegment(oldSegment.box(), new LinkedList<>());
      newSegments.push(newSegment);
    }

    while (lineSegments.size() > 1) {
      popElement();
    }

    return new LineBox(newSegments);
  }

}
