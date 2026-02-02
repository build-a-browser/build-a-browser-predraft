package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.composite.LayerUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.BorderUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.ManagedBoxEntryMarker;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.ManagedBoxExitMarker;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.StagedBlockLevelBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.StagedFloatBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.StagedUnmanagedBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.text.TextWrapModeValue;
import net.buildabrowser.babbrowser.css.engine.property.whitespace.WhitespaceCollapseValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class FlowInlineLayout {

  private final Deque<InlineFormattingContext> inlineStack = new ArrayDeque<>();
  private final FlowRootContent rootContent;

  public FlowInlineLayout(FlowRootContent rootContent) {
    this.rootContent = rootContent;
  }

  public void stopInline(
    LayoutContext layoutContext,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    ActiveStyles parentStyles
  ) {
    LineWhitespaceCollapser.collapseWhitespace(
      inlineStack.peek().stagingArea(),
      (WhitespaceCollapseValue) parentStyles.getProperty(CSSProperty.WHITE_SPACE_COLLAPSE));
    addStagedElements(layoutContext, widthConstraint, heightConstraint);
    inlineStack.pop().closeLine();
  }

  public void startInline(ActiveStyles parentStyles, LayoutConstraint widthConstraint) {
    inlineStack.push(new InlineFormattingContext(rootContent, widthConstraint, parentStyles));
  }

  // #region Staging

  public void stageInline(LayoutContext layoutContext, Box box) {
    InlineStagingArea stagingArea = inlineStack.peek().stagingArea();
    if (box instanceof TextBox textBox) {
      stagingArea.pushStagedElement(new StagedText(textBox, textBox.text()));
    } else if (box instanceof ElementBox elementBox) {
      // Might get computed twice for outer box, doesn't really matter
      LayoutConstraint widthConstraint = rootContent.blockLayout().activeContext().innerWidthConstraint();
      BorderUtil.computeBorder(layoutContext, elementBox, widthConstraint);
      PaddingUtil.computePadding(layoutContext, elementBox, widthConstraint);
      
      if (!PositionUtil.affectsLayout(elementBox)) {
        stagingArea.pushStagedElement(new StagedUnmanagedBox(elementBox));
      } else if (FlowUtil.isFloat(elementBox)) {
        stagingArea.pushStagedElement(new StagedFloatBox(elementBox));
      } else if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
        stagingArea.pushStagedElement(new StagedBlockLevelBox(elementBox));
      } else if (!FlowUtil.isInFlow(elementBox)) {
        stagingArea.pushStagedElement(new StagedUnmanagedBox(elementBox));
      } else {
        FlowWidthUtil.computeHorizontalMarginsOrZero(layoutContext, widthConstraint, elementBox);
        stagingArea.pushStagedElement(new ManagedBoxEntryMarker(elementBox));
        for (Box childBox: elementBox.childBoxes()) {
          stageInline(layoutContext, childBox);
        }
        stagingArea.pushStagedElement(new ManagedBoxExitMarker(elementBox));
      }
    } else {
      throw new UnsupportedOperationException("Unknown box type!");
    }
  }

  private void addStagedElements(LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    InlineStagingArea stagingArea = inlineStack.peek().stagingArea();
    stagingArea.resetCursor();
    ActiveStyles parentStyles = inlineStack.peek().activeStyles();
    while (!stagingArea.done()) {
      switch (stagingArea.next()) {
        case StagedText stagedText -> addTextToInline(layoutContext, parentStyles, stagedText);
        case StagedFloatBox stagedFloat -> addFloatAroundInline(
          layoutContext, stagedFloat.elementBox(), widthConstraint, heightConstraint);
        case StagedUnmanagedBox stagedUnmanagedBox -> {
          if (PositionUtil.affectsLayout(stagedUnmanagedBox.elementBox())) {
            addUnmanagedBlockToInline(
              layoutContext, stagedUnmanagedBox.elementBox(), widthConstraint, heightConstraint);
          } else {
            LayoutFragment newFragment = PositionLayout.layout(layoutContext, stagedUnmanagedBox.elementBox());
            InlineFormattingContext parentContext = inlineStack.peek();
            parentContext.addFragment(newFragment);
          }
        }
        case StagedBlockLevelBox stagedBlockLevelBox -> addBlockLevelToInline(
          layoutContext, stagedBlockLevelBox.elementBox(), widthConstraint, heightConstraint);
        case ManagedBoxEntryMarker marker -> inlineStack.peek().pushElement(marker.elementBox());
        case ManagedBoxExitMarker _ -> inlineStack.peek().popElement();
        default -> throw new UnsupportedOperationException("Unknown staging element type");
      }
    }
  }

  // #region Sizing

  private void addFloatAroundInline(
    LayoutContext layoutContext,
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    InlineFormattingContext inlineContext = inlineStack.peek();
    UnmanagedBoxFragment floatFragment = FloatLayout.renderFloat(
      layoutContext, elementBox, widthConstraint, heightConstraint);
    boolean fitsInLine = FloatLayout.addFloat(
      rootContent, floatFragment, widthConstraint, heightConstraint, inlineContext.lineBox().totalWidth());
    if (fitsInLine) return;

    inlineContext.nextLine();
    FloatLayout.addFloat(rootContent, floatFragment, widthConstraint, heightConstraint, 0);
  }

  private void addBlockLevelToInline(
    LayoutContext layoutContext,
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    inlineStack.peek().nextLine();
    rootContent.blockLayout().addToBlock(
      layoutContext, elementBox, widthConstraint, heightConstraint);
    rootContent.blockLayout().activeContext().collapse();
  }

  private void addUnmanagedBlockToInline(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        layoutContext, parentWidthConstraint, childBox) :
      FlowWidthUtil.determineInlineBlockNonReplacedWidthAndMargins(
        layoutContext, parentWidthConstraint, childBox);
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        layoutContext, parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        layoutContext, parentHeightConstraint, parentWidthConstraint, childBox);

    if (LayerUtil.startsLayer(childBox)) {
      layoutContext.stackingContext().start();
    }

    BoxFragment newFragment = parentWidthConstraint.isPreLayoutConstraint() ?
      new UnmanagedBoxFragment(
        FlowUtil.constraintWidth(childBox.dimensions(), parentWidthConstraint),
        FlowUtil.constraintHeight(childBox.dimensions(), parentHeightConstraint),
        childBox, null) :
      childBox.content().layout(layoutContext, childWidthConstraint, childHeightContraint);
    
    if (LayerUtil.startsLayer(childBox)) {
      newFragment.setPos(0, 0);
      newFragment = new PosRefBoxFragment(newFragment, layoutContext);
      layoutContext.stackingContext().end((PosRefBoxFragment) newFragment);
    }

    InlineFormattingContext parentContext = inlineStack.peek();
    parentContext.addFragment(newFragment);

    // TODO: Handle overflow
  }

  private void addTextToInline(
    LayoutContext layoutContext, ActiveStyles parentStyles, StagedText stagedText
  ) {
    String text = stagedText.currentText();
    if (text.isEmpty()) return;

    boolean autoWrap = parentStyles.getProperty(CSSProperty.TEXT_WRAP_MODE).equals(TextWrapModeValue.WRAP);
    FlowTextLayout.layoutText(layoutContext, stagedText, inlineStack.peek(), autoWrap);
  }

  // #region Positioning
  
  public void positionLine(LineBoxFragment fragment) {
    positionFragmentElements(fragment.fragments());
    int offsetX = rootContent.floatTracker().lineStartPos();
    rootContent.blockLayout().addFinishedFragment(null, fragment, offsetX);
  }

  private void positionFragmentElements(List<LayoutFragment> fragments) {
    int x = 0;
    for (LayoutFragment child: fragments) {
      child.setPos(0, 0); // Cheat to disable unset X assertions for next line
      int marginX = child.borderX() - child.marginX();
      // TODO: Is this the correct way to compute vertical positioning?
      int marginY = child.borderY() - child.marginY();
      child.setPos(x + marginX, marginY);

      if (!PositionUtil.affectsLayout(child)) continue;
      x += child.marginWidth();
      if (child instanceof ManagedBoxFragment managedBoxFragment) {
        positionFragmentElements(managedBoxFragment.fragments());
      }
    }
  }

}
