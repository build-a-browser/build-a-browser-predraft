package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.TextWrapper.TextWrapTarget;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class InlineFormattingContext implements TextWrapTarget, IntrusiveList<InlineFormattingContext> {
 
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
    ElementBox rootBox
  ) {
    this(flowContext, inlineConstraint, new LineBox(rootBox), new LinkedList<>());
    stylesStack.push(rootBox.properties());
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

  public void addFragment(
    LayoutFragment flowFragment,
    boolean isEmpty
  ) {
    activeLineBox.addFragment(flowFragment, isEmpty);
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
    addLineToBox(activeLineBox);
    drainPositionedQueue();
  }

  public void nextLine() {
    nextLine(true);
  }

  @Override
  public void nextLine(boolean isSoftWrap) {
    LineBox oldLineBox = this.activeLineBox;
    activeLineBox.markPreserved();
    this.activeLineBox = activeLineBox.split();
    addLineToBox(oldLineBox);
    drainPositionedQueue();
  }

  @Override
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

  @Override
  public boolean ignoreWhitespace() {
    return
      lineBox().isEmpty()
      && lineBox().collapseWhiteSpace();
  }

  @Override
  public void appendText(String text, int sourceIndex, float width, float height) {
    lineBox().appendText(text, sourceIndex, width, height);
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

  private void addLineToBox(LineBox lineBox) {
    LineBoxFragment lineBoxFragment = lineBox.toFragment();
    if (!lineBox.isEmpty()) {
      flowContext.blockLayout().activeContext().collapse();
    }

    FlowLinePositioner.positionLine(
      flowContext,
      lineBoxFragment,
      inlineConstraint,
      stylesStack.getFirst());
  }

  private void drainPositionedQueue() {
    if (positionedQueue.size() > 0) {
      flowContext.blockLayout().activeContext().collapse();
    }

    for (ElementBox positioned: positionedQueue) {
      flowContext.blockLayout().addPositionedToBlock(positioned);
    }
    positionedQueue.clear();
  }

}
