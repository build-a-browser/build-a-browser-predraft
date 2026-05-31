package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignValue;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeValue;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhitespaceCollapseValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.ManagedBoxEntryMarker;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.ManagedBoxExitMarker;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedBlockLevelBox;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedFloatBox;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedLineBreak;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedUnmanagedBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public class FlowInlineLayout {

  private final FlowRootContent rootContent;

  // Is a stack
  private InlineFormattingContext activeInlineContext;

  public FlowInlineLayout(FlowRootContent rootContent) {
    this.rootContent = rootContent;
  }

  public void reset() {
    this.activeInlineContext = null;
  }

  public void stopInline(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    ActiveStyles parentStyles
  ) {
    LineWhitespaceCollapser.collapseWhitespace(
      activeInlineContext.stagingArea(),
      (WhitespaceCollapseValue) parentStyles.getProperty(CSSProperty.WHITE_SPACE_COLLAPSE));
    addStagedElements(widthConstraint, heightConstraint);
    activeInlineContext.closeLine();
    activeInlineContext = activeInlineContext.next();
  }

  public void startInline(ActiveStyles parentStyles, LayoutConstraint widthConstraint) {
    activeInlineContext = IntrusiveList.push(
      activeInlineContext,
      new InlineFormattingContext(rootContent, widthConstraint, parentStyles));
  }

  // #region Staging

  public void stageInline(LayoutContext parentContext, Box box) {
    InlineStagingArea stagingArea = activeInlineContext.stagingArea();
    if (box instanceof TextBox textBox) {
      stagingArea.pushStagedElement(new StagedText(parentContext, textBox, textBox.text()));
    } else if (box instanceof ElementBox elementBox) {
      // Might get computed twice for outer box, doesn't really matter
      LayoutConstraint widthConstraint = rootContent.blockLayout().activeContext().innerWidthConstraint();
      elementBox.content().computeMeasures(elementBox, widthConstraint);
      
      if (!PositionUtil.affectsLayout(elementBox)) {
        stagingArea.pushStagedElement(new StagedUnmanagedBox(elementBox));
      } else if (FlowUtil.isFloat(elementBox)) {
        stagingArea.pushStagedElement(new StagedFloatBox(elementBox));
      } else if (elementBox.element() != null && elementBox.element().name().equals("br")) {
        // TODO: The spec says br is display-outside: newline, but that is not a valid display mode
        stagingArea.pushStagedElement(new StagedLineBreak(elementBox.layoutContext()));
      } else if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
        stagingArea.pushStagedElement(new StagedBlockLevelBox(elementBox));
      } else if (!FlowUtil.isInFlow(elementBox)) {
        stagingArea.pushStagedElement(new StagedUnmanagedBox(elementBox));
      } else {
        FlowWidthUtil.computeHorizontalMarginsOrZero(widthConstraint, elementBox);
        stagingArea.pushStagedElement(new ManagedBoxEntryMarker(elementBox));
        for (Box childBox: elementBox.childBoxes()) {
          stageInline(elementBox.layoutContext(), childBox);
        }
        stagingArea.pushStagedElement(new ManagedBoxExitMarker(elementBox));
      }
    } else {
      throw new UnsupportedOperationException("Unknown box type!");
    }
  }

  // TODO: Handle float clear?
  private void addStagedElements(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    InlineStagingArea stagingArea = activeInlineContext.stagingArea();
    stagingArea.resetCursor();
    ActiveStyles parentStyles = activeInlineContext.activeStyles();
    while (!stagingArea.done()) {
      switch (stagingArea.next()) {
        case StagedText stagedText -> addTextToInline(stagedText.layoutContext(), parentStyles, stagedText);
        case StagedLineBreak stagedBreak -> addBreakToInline(stagedBreak.layoutContext());
        case StagedFloatBox stagedFloat -> addFloatAroundInline(
          stagedFloat.elementBox(), widthConstraint, heightConstraint);
        case StagedUnmanagedBox stagedUnmanagedBox -> {
          if (PositionUtil.affectsLayout(stagedUnmanagedBox.elementBox())) {
            addUnmanagedBlockToInline(
              stagedUnmanagedBox.elementBox(), widthConstraint, heightConstraint);
          } else {
            LayoutFragment newFragment = PositionLayout.layout(stagedUnmanagedBox.elementBox());
            activeInlineContext.addFragment(newFragment);
          }
        }
        case StagedBlockLevelBox stagedBlockLevelBox -> addBlockLevelToInline(
          stagedBlockLevelBox.elementBox(), widthConstraint, heightConstraint);
        case ManagedBoxEntryMarker marker -> activeInlineContext.pushElement(marker.elementBox());
        case ManagedBoxExitMarker _1 -> activeInlineContext.popElement();
        default -> throw new UnsupportedOperationException("Unknown staging element type");
      }
    }
  }

  // #region Sizing

  private void addFloatAroundInline(
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    UnmanagedBoxFragment floatFragment = FloatLayout.renderFloat(
      elementBox, widthConstraint, heightConstraint);
    boolean fitsInLine = FloatLayout.addFloat(
      rootContent, floatFragment, widthConstraint, heightConstraint, activeInlineContext.lineBox().totalWidth());
    if (fitsInLine) return;

    activeInlineContext.nextLine();
    FloatLayout.addFloat(rootContent, floatFragment, widthConstraint, heightConstraint, 0);
  }

  private void addBlockLevelToInline(
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    activeInlineContext.nextLine();
    rootContent.blockLayout().addToBlock(
      elementBox, widthConstraint, heightConstraint);
    rootContent.blockLayout().activeContext().collapse();
  }

  private void addUnmanagedBlockToInline(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        parentWidthConstraint, childBox) :
      FlowWidthUtil.determineInlineBlockNonReplacedWidthAndMargins(
        parentWidthConstraint, childBox);
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint, childBox);

    BoxFragment newFragment = parentWidthConstraint.isPreLayoutConstraint() ?
      new UnmanagedBoxFragment(
        FlowUtil.constraintWidth(childBox.dimensions(), parentWidthConstraint),
        FlowUtil.constraintHeight(childBox.dimensions(), parentHeightConstraint),
        childBox) :
      childBox.layout(childWidthConstraint, childHeightContraint);

    if (!activeInlineContext.fits(newFragment.width(Measurement.MARGIN), true)) {
      activeInlineContext.nextLine();
    }
    activeInlineContext.addFragment(newFragment);
  }

  private void addTextToInline(
    LayoutContext layoutContext, ActiveStyles parentStyles, StagedText stagedText
  ) {
    String text = stagedText.currentText();
    if (text.isEmpty()) return;

    boolean autoWrap = parentStyles.getProperty(CSSProperty.TEXT_WRAP_MODE).equals(TextWrapModeValue.WRAP);
    FlowTextLayout.layoutText(layoutContext, stagedText, activeInlineContext, autoWrap);
  }

  private void addBreakToInline(LayoutContext layoutContext) {
    InlineFormattingContext inlineContext = activeInlineContext;
    if (inlineContext.lineBox().totalHeight() == 0) {
      inlineContext.lineBox().appendText("\u200B", 0, layoutContext.font().metrics().height());
    }
    inlineContext.nextLine();
  }

  // #region Positioning
  
  public void positionLine(
    LineBoxFragment fragment,
    LayoutConstraint inlineConstraint,
    ActiveStyles lineStyles
  ) {
    positionFragmentElements(fragment.fragments(), inlineConstraint);
    float startPos = rootContent.floatTracker().lineStartPos();
    float inlineOffset = inlineConstraint.isBounded() ?
      alignFragment(
        lineStyles, startPos,
        rootContent.floatTracker().lineEndPos(inlineConstraint),
        fragment.width(Measurement.CONTENT)) :
      startPos;
    rootContent.blockLayout().addFinishedFragment(
      fragment, inlineOffset, inlineConstraint);
  }

  private void positionFragmentElements(
    LayoutFragment fragments,
    LayoutConstraint relatedConstraint
  ) {
    float x = 0;

    LayoutFragment nextChild = fragments;
    while (nextChild != null) {
      LayoutFragment child = nextChild;
      nextChild = nextChild.next();

      child.setPos(0, 0); // Cheat to disable unset X assertions for next line
      float marginX = child.posX(Measurement.BORDER) - child.posX(Measurement.MARGIN);
      // TODO: Is this the correct way to compute vertical positioning?
      float marginY = child.posY(Measurement.BORDER) - child.posY(Measurement.MARGIN);
      child.setPos(x + marginX, marginY);

      if (!PositionUtil.affectsLayout(child)) continue;
      x += child.width(Measurement.MARGIN);
      if (child instanceof ManagedBoxFragment managedBoxFragment) {
        positionFragmentElements(managedBoxFragment.fragments(), relatedConstraint);
      }
    }
  }

  private float alignFragment(ActiveStyles lineStyles, float startPos, float endPos, float lineWidth) {
    TextAlignValue textAlign = (TextAlignValue) lineStyles.getProperty(CSSProperty.TEXT_ALIGN);
    while (
      textAlign.equals(TextAlignValue.MATCH_PARENT)
      && lineStyles.parent() != null
    ) {
      lineStyles = lineStyles.parent();
      textAlign = (TextAlignValue) lineStyles.getProperty(CSSProperty.TEXT_ALIGN);
    }


    return switch (textAlign) {
      // TODO: Once rtl is supported, obey rtl
      case START -> startPos;
      case END -> endPos - lineWidth;

      case LEFT -> startPos;
      case CENTER -> startPos + (endPos - startPos) / 2 - lineWidth / 2;
      case RIGHT -> endPos - lineWidth;

      // TODO: Properly implement these
      case JUSTIFY -> startPos;
      case JUSTIFY_ALL -> startPos;
      // MATCH_PARENT remains unresolved, default to START
      case MATCH_PARENT -> startPos;

      default -> throw new UnsupportedOperationException("Unrecognized value: " + textAlign);
    };
  }

}
