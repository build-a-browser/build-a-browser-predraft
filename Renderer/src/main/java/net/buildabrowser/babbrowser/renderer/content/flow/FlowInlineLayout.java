package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeValue;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhitespaceCollapseValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.ManagedBoxEntryMarker;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.ManagedBoxExitMarker;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedBlockLevelBox;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedFloatBox;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedLineBreak;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedUnmanagedBox;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public class FlowInlineLayout {

  private final FlowContext flowContext;

  // Is a stack
  private InlineFormattingContext activeInlineContext;

  public FlowInlineLayout(FlowContext flowContext) {
    this.flowContext = flowContext;
  }

  public void stopInline(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    PropertyContainer properties
  ) {
    LineWhitespaceCollapser.collapseWhitespace(
      activeInlineContext.stagingArea(),
      (WhitespaceCollapseValue) properties.get(CSSProperty.WHITE_SPACE_COLLAPSE));
    addStagedElements(widthConstraint, heightConstraint);
    activeInlineContext.closeLine();
    activeInlineContext = activeInlineContext.next();
  }

  public void startInline(
    ElementBox rootBox,
    LayoutConstraint widthConstraint
  ) {
    activeInlineContext = IntrusiveList.push(
      activeInlineContext,
      new InlineFormattingContext(flowContext, widthConstraint, rootBox));
  }

  // #region Staging

  public void stageInline(LayoutContext parentContext, Box box) {
    InlineStagingArea stagingArea = activeInlineContext.stagingArea();
    if (box instanceof TextBox textBox) {
      stagingArea.pushStagedElement(new StagedText(parentContext, textBox, textBox.text()));
    } else if (box instanceof ElementBox elementBox) {
      // Might get computed twice for outer box, doesn't really matter
      LayoutConstraint widthConstraint = flowContext.blockLayout().activeContext().innerWidthConstraint();
      elementBox.content().computeMeasures(elementBox, widthConstraint);
      
      if (!PositionUtil.affectsLayout(elementBox)) {
        stagingArea.pushStagedElement(new StagedUnmanagedBox(elementBox));
      } else if (FlowUtil.isFloat(elementBox)) {
        stagingArea.pushStagedElement(new StagedFloatBox(elementBox));
      } else if (elementBox.element() != null && elementBox.element().name().equals("br")) {
        // TODO: The spec says br is display-outside: newline, but that is not a valid display mode
        stagingArea.pushStagedElement(StagedLineBreak.create());
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
    PropertyContainer parentProperties = activeInlineContext.properties();
    while (!stagingArea.done()) {
      switch (stagingArea.next()) {
        case StagedText stagedText -> addTextToInline(stagedText.layoutContext(), parentProperties, stagedText);
        case StagedLineBreak stagedBreak -> addBreakToInline();
        case StagedFloatBox stagedFloat -> addFloatAroundInline(
          stagedFloat.elementBox(), widthConstraint, heightConstraint);
        case StagedUnmanagedBox stagedUnmanagedBox -> addUnmanagedToInline(
          stagedUnmanagedBox.elementBox(), widthConstraint, heightConstraint);
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
    UnmanagedBoxFragment<?> floatFragment = FloatLayout.renderFloat(
      elementBox, widthConstraint, heightConstraint);
    boolean fitsInLine = FloatLayout.addFloat(
      flowContext, floatFragment, widthConstraint, heightConstraint, activeInlineContext.lineBox().totalWidth());
    if (!fitsInLine) {
      activeInlineContext.nextLine();
      FloatLayout.addFloat(flowContext, floatFragment, widthConstraint, heightConstraint, 0);
    }

    activeInlineContext.addFragment(new FloatRefFragment(floatFragment));
  }

  private void addUnmanagedToInline(
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    if (PositionUtil.affectsLayout(elementBox)) {
      addUnmanagedBlockToInline(
        elementBox, widthConstraint, heightConstraint);
    } else if (elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)) {
      activeInlineContext.queuedPositioned(elementBox);
    } else {
      LayoutFragment newFragment = PositionLayout.layout(elementBox);
      activeInlineContext.addFragment(newFragment);
    }
  }

  private void addBlockLevelToInline(
    ElementBox elementBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    activeInlineContext.nextLine();
    flowContext.blockLayout().addToBlock(
      elementBox, widthConstraint, heightConstraint);
    flowContext.blockLayout().activeContext().collapse();
  }

  private void addUnmanagedBlockToInline(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        parentWidthConstraint, parentHeightConstraint, childBox) :
      FlowWidthUtil.determineInlineBlockNonReplacedWidthAndMargins(
        parentWidthConstraint, childBox);
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint, childBox);

    FragmentFactory fragmentFactory = childBox.layoutContext().global().fragmentFactory();
    BoxFragment<?> newFragment = parentWidthConstraint.isPreLayoutConstraint() ?
      fragmentFactory.createGenericUnmanagedBox(
        FlowUtil.constraintWidth(childBox, parentWidthConstraint),
        FlowUtil.constraintHeight(childBox, parentHeightConstraint),
        childBox) :
      childBox.layout(childWidthConstraint, childHeightContraint);

    if (!activeInlineContext.fits(newFragment.width(Measurement.MARGIN), true)) {
      activeInlineContext.nextLine();
    }
    activeInlineContext.addFragment(newFragment);
  }

  private void addTextToInline(
    LayoutContext layoutContext,
    PropertyContainer parentProperties,
    StagedText stagedText
  ) {
    String text = stagedText.currentText();
    if (text.isEmpty()) return;

    boolean autoWrap = parentProperties.get(CSSProperty.TEXT_WRAP_MODE).equals(TextWrapModeValue.WRAP);
    FlowTextLayout.layoutText(layoutContext, stagedText, activeInlineContext, autoWrap);
  }

  private void addBreakToInline() {
    activeInlineContext.nextLine();
  }

}
