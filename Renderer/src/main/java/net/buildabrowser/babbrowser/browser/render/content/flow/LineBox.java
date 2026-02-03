package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionUtil;

public class LineBox {

  private final FlowTextFragmentBuilder textBuilder = new FlowTextFragmentBuilder();
  
  private final Deque<LineSegment> lineSegments;

  public LineBox() {
    this.lineSegments = new LinkedList<>();
    lineSegments.push(new LineSegment(null, new LinkedList<>()));
  }

  private LineBox(Deque<LineSegment> segments) {
    this.lineSegments = segments;
  }

  private float totalWidth = 0;
  
  public void addFragment(LayoutFragment fragment) {
    commitText();
    if (PositionUtil.affectsLayout(fragment)) {
      this.totalWidth += fragment.marginWidth();
    }
    lineSegments.peek().fragments().add(fragment);
  }

  public void appendText(String text, float width, float height) {
    this.totalWidth += width;
    textBuilder.addText(text, width, height);
  }

  public void pushElement(ElementBox elementBox) {
    commitText();
    this.totalWidth +=
      elementBox.dimensions().getComputedMargin()[2] +
      elementBox.dimensions().getComputedBorder()[2] +
      elementBox.dimensions().getComputedPadding()[2];
    lineSegments.push(new LineSegment(elementBox, new LinkedList<>()));
  }

  public ElementBox popElement() {
    commitText();
    LineSegment lineSegment = lineSegments.pop();
    ManagedBoxFragment managedBoxFragment = new ManagedBoxFragment(
      lineSegment.width(), lineSegment.height(),
      lineSegment.box(), FlowRootContentPainter.FLOW_INLINE_PAINTER,
      lineSegment.fragments());
    lineSegments.peek().fragments().add(managedBoxFragment);
    
    this.totalWidth +=
      lineSegment.box().dimensions().getComputedMargin()[3] +
      lineSegment.box().dimensions().getComputedBorder()[3] +
      lineSegment.box().dimensions().getComputedPadding()[3];
    return managedBoxFragment.box();
  }

  public float totalWidth() {
    return this.totalWidth;
  }

  // TODO: This is surely wrong...
  public float totalHeight() {
    float totalHeight = textBuilder.height();
    for (LineSegment segment: this.lineSegments) {
      totalHeight = Math.max(totalHeight, segment.height());
    }

    return totalHeight;
  }

  public LineBoxFragment toFragment() {
    commitText();
    LineSegment activeSegment = lineSegments.peek();
    return new LineBoxFragment(totalWidth, activeSegment.height(), activeSegment.fragments());
  }

  public LineBox split() {
    commitText();
    Deque<LineSegment> newSegments = new LinkedList<>();
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

  private void commitText() {
    if (!textBuilder.isEmpty()) {
      lineSegments.peek().fragments().add(textBuilder.commit());
    }
  }

}
