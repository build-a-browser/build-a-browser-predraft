package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappingRLEBuffer;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowInlineBoxFragment;

public class LineBox {

  private final FlowTextFragmentBuilder textBuilder;
  
  private final Deque<LineSegment> lineSegments;

  public LineBox(ElementBox rootBox) {
    this.textBuilder = new FlowTextFragmentBuilder();
    this.lineSegments = new LinkedList<>();
    lineSegments.push(new LineSegment(rootBox));
  }

  private LineBox(
    FlowTextFragmentBuilder textBuilder,
    Deque<LineSegment> segments
  ) {
    this.textBuilder = textBuilder;
    this.lineSegments = segments;
  }

  private float totalWidth = 0;
  
  public void addFragment(
    LayoutFragment fragment,
    boolean isEmpty
  ) {
    commitText();
    if (PositionUtil.affectsLayout(fragment)) {
      this.totalWidth += fragment.width(Measurement.MARGIN);
    }
    lineSegments.peek().addFragment(fragment, isEmpty);
  }

  public void startText(
    Node sourceNode,
    MappingRLEBuffer buffer
  ) {
    commitText();
    textBuilder.startText(sourceNode, buffer);
  }

  public void appendText(
    String text, int sourceIndex,
    float width, float height
  ) {
    this.totalWidth += width;
    textBuilder.addText(text, sourceIndex, width, height);
  }

  public void pushElement(ElementBox elementBox) {
    commitText();
    this.totalWidth +=
      elementBox.dimensions().getComputedMargin()[2] +
      elementBox.dimensions().getComputedBorder()[2] +
      elementBox.dimensions().getComputedPadding()[2];
    lineSegments.push(new LineSegment(elementBox));
  }

  public ElementBox popElement() {
    commitText();
    LineSegment lineSegment = lineSegments.pop();
    FlowInlineBoxFragment inlineBoxFragment = lineSegment.toFragment();
    lineSegments.peek().addFragment(inlineBoxFragment, lineSegment.isEmpty());
    
    this.totalWidth +=
      lineSegment.box().dimensions().getComputedMargin()[3] +
      lineSegment.box().dimensions().getComputedBorder()[3] +
      lineSegment.box().dimensions().getComputedPadding()[3];
    return inlineBoxFragment.box();
  }

  public float totalWidth() {
    return this.totalWidth;
  }

  public LineBoxFragment toFragment() {
    commitText();
    LineSegment activeSegment = lineSegments.peek();
    FlowInlineBoxFragment inlineBoxFragment = activeSegment.toFragment();
    // TODO: Why wasn't LineBoxFragment tracking the ink size?
    return new LineBoxFragment(
      inlineBoxFragment.width(Measurement.CONTENT),
      inlineBoxFragment.height(Measurement.CONTENT),
      inlineBoxFragment.firstBaseline(Measurement.CONTENT),
      inlineBoxFragment.lastBaseline(Measurement.CONTENT),
      inlineBoxFragment.fragments());
  }

  public LineBox split() {
    commitText();
    Deque<LineSegment> newSegments = new LinkedList<>();
    Iterator<LineSegment> it = lineSegments.descendingIterator();
    while (it.hasNext()) {
      LineSegment oldSegment = it.next();
      LineSegment newSegment = new LineSegment(oldSegment.box());
      newSegments.push(newSegment);
    }

    while (lineSegments.size() > 1) {
      popElement();
    }

    return new LineBox(textBuilder, newSegments);
  }

  private void commitText() {
    if (!textBuilder.isEmpty()) {
      FontMetrics metrics = lineSegments.peek().box().layoutContext().font().metrics();
      TextFragment textFragment = textBuilder.commit(metrics);
      // TODO: Trim removes some control characters that should be kept
      boolean isEmpty = textFragment.text().trim().length() == 0;
      lineSegments.peek().addFragment(textFragment, isEmpty);
    }
  }

}
