package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.Deque;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class InlineFormattingContext implements IntrusiveList<InlineFormattingContext> {
 
  private final FlowRootContent rootContent;
  private final LayoutConstraint inlineConstraint;
  private final InlineStagingArea stagingArea;
  private final Deque<ActiveStyles> stylesStack;

  private InlineFormattingContext next;
  private LineBox activeLineBox;

  public InlineFormattingContext(
    FlowRootContent rootContent,
    LayoutConstraint inlineConstraint,
    ActiveStyles initialStyles
  ) {
    this(rootContent, inlineConstraint, new LineBox(), new LinkedList<>());
    stylesStack.push(initialStyles);
  }

  private InlineFormattingContext(
    FlowRootContent rootContent,
    LayoutConstraint inlineConstraint,
    LineBox firstLineBox,
    Deque<ActiveStyles> stylesStack
  ) {
    this.rootContent = rootContent;
    this.inlineConstraint = inlineConstraint;
    this.stagingArea = new InlineStagingArea();
    this.stylesStack = stylesStack;
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
    stylesStack.push(elementBox.activeStyles());
  }

  public ElementBox popElement() {
    stylesStack.pop();
    return activeLineBox.popElement();
  }

  public LineBox lineBox() {
    return this.activeLineBox;
  }

  public void closeLine() {
    rootContent.inlineLayout().positionLine(
      activeLineBox.toFragment(), inlineConstraint, stylesStack.getFirst());
  }

  public void nextLine() {
    LineBox oldLineBox = this.activeLineBox;
    this.activeLineBox = activeLineBox.split();
    rootContent.inlineLayout().positionLine(
      oldLineBox.toFragment(), inlineConstraint, stylesStack.getFirst());
  }

  public boolean fits(float itemSize, boolean forceFirst) {
    if (forceFirst && this.activeLineBox.totalWidth() == 0) {
      return true;
    }
    
    FloatTracker floatTracker = rootContent.floatTracker();
    return switch (inlineConstraint.type()) {
      case MIN_CONTENT -> false;
      case MAX_CONTENT, AUTO -> true;
      case BOUNDED -> floatTracker.lineStartPos() + this.activeLineBox.totalWidth() + itemSize
        <= floatTracker.lineEndPos(inlineConstraint);
      default -> throw new UnsupportedOperationException("Unrecognized Layout Constraint: " + inlineConstraint);
    };
  }

  public ActiveStyles activeStyles() {
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

}
