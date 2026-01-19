package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayDeque;
import java.util.Deque;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class InlineFormattingContext {
 
  private final FlowRootContent rootContent;
  private final LayoutConstraint inlineConstraint;
  private final InlineStagingArea stagingArea;
  private final Deque<ActiveStyles> stylesStack;
  private LineBox activeLineBox;

  public InlineFormattingContext(
    FlowRootContent rootContent,
    LayoutConstraint inlineConstraint,
    ActiveStyles initialStyles
  ) {
    this(rootContent, inlineConstraint, new LineBox(), new ArrayDeque<>());
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

  public void addFragment(FlowFragment flowFragment) {
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
    rootContent.inlineLayout().positionLine(activeLineBox.toFragment());
  }

  public void nextLine() {
    LineBox oldLineBox = this.activeLineBox;
    this.activeLineBox = activeLineBox.split();
    rootContent.inlineLayout().positionLine(oldLineBox.toFragment());
  }

  public boolean fits(int itemSize, boolean forceFirst) {
    if (forceFirst && this.activeLineBox.totalWidth() == 0) {
      return true;
    }
    
    FloatTracker floatTracker = rootContent.floatTracker();
    return switch (inlineConstraint.type()) {
      case MIN_CONTENT -> false;
      case MAX_CONTENT, AUTO -> true;
      case BOUNDED -> floatTracker.lineStartPos() + this.activeLineBox.totalWidth() + itemSize
        <= floatTracker.lineEndPos(inlineConstraint);
    };
  }

  public ActiveStyles activeStyles() {
    return stylesStack.peek();
  }

}
