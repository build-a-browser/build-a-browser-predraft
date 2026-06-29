package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class InlineFormattingContext implements IntrusiveList<InlineFormattingContext> {
 
  private final FlowContext flowContext;
  private final LayoutConstraint inlineConstraint;
  private final InlineStagingArea stagingArea;
  private final Deque<PropertyContainer> stylesStack;
  private final List<ElementBox> positionedQueue;

  private InlineFormattingContext next;
  private LineBox activeLineBox;

  public InlineFormattingContext(
    FlowContext flowContext,
    LayoutConstraint inlineConstraint,
    PropertyContainer properties
  ) {
    this(flowContext, inlineConstraint, new LineBox(), new LinkedList<>());
    stylesStack.push(properties);
  }

  private InlineFormattingContext(
    FlowContext flowContext,
    LayoutConstraint inlineConstraint,
    LineBox firstLineBox,
    Deque<PropertyContainer> stylesStack
  ) {
    this.flowContext = flowContext;
    this.inlineConstraint = inlineConstraint;
    this.stagingArea = new InlineStagingArea();
    this.stylesStack = stylesStack;
    this.positionedQueue = new ArrayList<>();
    this.activeLineBox = firstLineBox;
  }

  public InlineStagingArea stagingArea() {
    return this.stagingArea;
  }

  public void addFragment(LayoutFragment flowFragment) {
    activeLineBox.addFragment(flowFragment);
  }

  public void pushElement(ElementBox elementBox) {
    activeLineBox.pushElement(elementBox);
    stylesStack.push(elementBox.properties());
  }

  public ElementBox popElement() {
    stylesStack.pop();
    return activeLineBox.popElement();
  }

  public LineBox lineBox() {
    return this.activeLineBox;
  }

  public void closeLine() {
    flowContext.inlineLayout().positionLine(
      activeLineBox.toFragment(), inlineConstraint, stylesStack.getFirst());
    drainPositionedQueue();
  }

  public void nextLine() {
    LineBox oldLineBox = this.activeLineBox;
    this.activeLineBox = activeLineBox.split();
    flowContext.inlineLayout().positionLine(
      oldLineBox.toFragment(), inlineConstraint, stylesStack.getFirst());
    drainPositionedQueue();
  }

  public boolean fits(float itemSize, boolean forceFirst) {
    if (forceFirst && this.activeLineBox.totalWidth() == 0) {
      return true;
    }
    
    FloatTracker floatTracker = flowContext.floatTracker();
    return switch (inlineConstraint.type()) {
      case MIN_CONTENT -> false;
      case MAX_CONTENT, AUTO -> true;
      case BOUNDED -> floatTracker.lineStartPos() + this.activeLineBox.totalWidth() + itemSize
        <= floatTracker.lineEndPos(inlineConstraint);
      default -> throw new UnsupportedOperationException("Unrecognized Layout Constraint: " + inlineConstraint);
    };
  }

  public void queuedPositioned(ElementBox box) {
    positionedQueue.add(box);
  }

  public PropertyContainer properties() {
    return stylesStack.peek();
  }

  @Override
  public InlineFormattingContext next() {
    return this.next;
  }

  @Override
  public void setNext(InlineFormattingContext nextNode) {
    this.next = nextNode;
  }

  private void drainPositionedQueue() {
    for (ElementBox positioned: positionedQueue) {
      flowContext.blockLayout().addPositionedToBlock(positioned);
    }
    positionedQueue.clear();
  }

}
