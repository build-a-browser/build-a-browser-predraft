package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flexbox.FlexCrossAlignment.CrossAlignmentContext;
import net.buildabrowser.babbrowser.browser.render.content.flexbox.FlexMainAlignment.MainAlignmentContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentValue;

public class FlexBoxContent implements BoxContent {

  private final FlexBoxContentPainter painter = new FlexBoxContentPainter(this);

  private final ElementBox rootBox;

  private UnmanagedBoxFragment fragments;

  public FlexBoxContent(ElementBox rootBox) {
    this.rootBox = rootBox;
  }

  @Override
  public void fixupChildren() {
    ElementBoxIterator childIt = rootBox.childBoxes();
    ElementBox anonymousBox = null;
    boolean isOnlyWhitespace = true;
    boolean didInsertBox = false;
    while (childIt.hasNext()) {
      switch (childIt.next()) {
        case ElementBox _ -> {
          anonymousBox = null;
          didInsertBox = false;
        }
        case TextBox textBox -> {
          if (anonymousBox == null) {
            isOnlyWhitespace = true;
            // It's actually flex-level, but this flag has no effect regardless
            anonymousBox = ElementBox.createAnonymous(ActiveStyles.create(), anonymousBox, BoxLevel.BLOCK_LEVEL);
          }
          isOnlyWhitespace = isOnlyWhitespace && textBox.text().isBlank(); // TODO: Proper HTML whitespace
          childIt.remove();
          anonymousBox.addChild(textBox);
        }
        default -> throw new UnsupportedOperationException("Don't know how to handle this type of box!");
      }

      if (
        anonymousBox != null
        && !isOnlyWhitespace
        && !didInsertBox
      ) {
        childIt.add(anonymousBox);
      }
    }
  }

  // TODO: Test how well this code handles positioned items..

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    // TODO: Also support gap
    List<FlexItem> flexItems = collectFlexItems();
    return layoutItems(layoutContext, flexItems, widthConstraint, heightConstraint);
  }

  public UnmanagedBoxFragment fragments() {
    return this.fragments;
  }

  private UnmanagedBoxFragment layoutItems(
    LayoutContext layoutContext, List<FlexItem> items, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    FlexDirectionValue flexDirection = (FlexDirectionValue) rootBox.activeStyles().getProperty(CSSProperty.FLEX_DIRECTION);
    boolean isVertical = flexDirection.equals(FlexDirectionValue.COLUMN) || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
    LayoutConstraint mainSize = isVertical ? heightConstraint : widthConstraint;
    LayoutConstraint crossSize = isVertical ? widthConstraint : heightConstraint;
    FlexHypotheticalSizeDetermination.determineBaseAndHypotheticalSizes(layoutContext, items, mainSize, crossSize, isVertical);
    List<FlexLine> lines = collectFlexItemsIntoFlexLines(mainSize, items);
    for (FlexLine line: lines) {
      Flexer.flex(mainSize, line);
    }
    if (!widthConstraint.isPreLayoutConstraint()) {
      FlexCrossSizeDetermination.determineCrossSize(layoutContext, rootBox, lines, crossSize, isVertical);

      JustifyContentValue contentJustification = (JustifyContentValue) rootBox.activeStyles().getProperty(CSSProperty.JUSTIFY_CONTENT);
      boolean isReverse =
        flexDirection.equals(FlexDirectionValue.ROW_REVERSE)
        || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
      FlexMainAlignment.alignMainAxis(
        new MainAlignmentContext(layoutContext, mainSize, isVertical, isReverse, contentJustification),
        lines);
      AlignContentValue alignContent = (AlignContentValue) rootBox.activeStyles().getProperty(CSSProperty.ALIGN_CONTENT);
      FlexCrossAlignment.alignCrossAxis(
        new CrossAlignmentContext(layoutContext, crossSize, isVertical, alignContent),
        lines);

      collectedChildFragments(items);
    }

    return createRootFragment(isVertical, mainSize, crossSize, lines);
  }

  private List<FlexItem> collectFlexItems() {
    List<FlexItem> items = new ArrayList<>();
    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      // Anonymous box generation is handled during fixupChildren
      items.add(new FlexItem((ElementBox) childIt.next()));
    }

    items.sort((a, b) -> Integer.compare(orderOf(a), orderOf(b)));

    return items;
  }

  private int orderOf(FlexItem item) {
    return ((OrderValue) item.box().activeStyles().getProperty(CSSProperty.ORDER)).order();
  }

  private List<FlexLine> collectFlexItemsIntoFlexLines(LayoutConstraint mainConstraint, List<FlexItem> flexItems) {
    List<FlexLine> lines = new LinkedList<>();
    FlexLine activeLine = new FlexLine();
    if (rootBox.activeStyles().getProperty(CSSProperty.FLEX_WRAP).equals(FlexWrapValue.NOWRAP)) {
      for (FlexItem item: flexItems) {
        activeLine.addItem(item);
      }
    } else {
      float lineSize = 0;
      for (FlexItem item: flexItems) {
        // TODO: Must borders be added for the outer size?
        if (
          !activeLine.isEmpty()
          && !mainConstraint.type().equals(LayoutConstraintType.MAX_CONTENT)
          && lineSize + item.hypotheticalMainSize() > mainConstraint.value()
        ) {
          lines.add(activeLine);
          lineSize = 0;
          activeLine = new FlexLine();
        }

        activeLine.addItem(item);
        lineSize += item.hypotheticalMainSize();
      }
    }

    lines.add(activeLine);
    return lines;
  }

  private void collectedChildFragments(List<FlexItem> items) {
    fragments = null;
    for (FlexItem item: items) {
      fragments = (UnmanagedBoxFragment) IntrusiveList.add(fragments, item.fragment());
    }
  }

  private UnmanagedBoxFragment createRootFragment(
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize, List<FlexLine> lines
  ) {
    float largestLineMain = 0;
    float largestLineCross = 0;
    for (FlexLine line: lines) {
      largestLineMain = Math.max(line.sumHypotheticalMainSizes(), largestLineCross);
      largestLineCross += line.crossSize();
    }
    
    float resolvedMain = LayoutUtil.constraintOrDim(mainSize, largestLineMain);
    float resolvedCross = LayoutUtil.constraintOrDim(crossSize, largestLineCross);
    return new UnmanagedBoxFragment(
      isVertical ? resolvedCross : resolvedMain,
      isVertical ? resolvedMain : resolvedCross, rootBox, painter);
  }
  
}
