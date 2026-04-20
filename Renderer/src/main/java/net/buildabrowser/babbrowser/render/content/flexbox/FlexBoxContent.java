package net.buildabrowser.babbrowser.render.content.flexbox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentValue;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.render.box.TextBox;
import net.buildabrowser.babbrowser.render.content.common.BorderUtil;
import net.buildabrowser.babbrowser.render.content.common.MarginUtil;
import net.buildabrowser.babbrowser.render.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.render.content.flexbox.FlexCrossAlignment.CrossAlignmentContext;
import net.buildabrowser.babbrowser.render.content.flexbox.FlexMainAlignment.MainAlignmentContext;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

public class FlexBoxContent implements BoxContent {

  private static final EventHandler EVENT_HANDLER = new FlexBoxEventHandler();

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
        didInsertBox = true;
      }
    }

    rootBox.sortChildren((a, b) -> Integer.compare(orderOf(a), orderOf(b)));
  }

  // TODO: Test how well this code handles positioned items..

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    // TODO: Also support gap
    List<FlexItem> flexItems = collectFlexItems();
    return layoutItems(flexItems, widthConstraint, heightConstraint);
  }

  @Override
  public EventHandler eventHandler() {
    return EVENT_HANDLER;
  }

  public UnmanagedBoxFragment fragments() {
    return this.fragments;
  }

  private UnmanagedBoxFragment layoutItems(
    List<FlexItem> items, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    FlexDirectionValue flexDirection = (FlexDirectionValue) rootBox.activeStyles().getProperty(CSSProperty.FLEX_DIRECTION);
    boolean isVertical = flexDirection.equals(FlexDirectionValue.COLUMN) || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
    LayoutConstraint mainSize = isVertical ? heightConstraint : widthConstraint;
    LayoutConstraint crossSize = isVertical ? widthConstraint : heightConstraint;

    for (FlexItem item: items) {
      MarginUtil.computeSimpleMargin(item.box(), widthConstraint);
      BorderUtil.computeBorder(item.box(), widthConstraint);
      PaddingUtil.computePadding(item.box(), widthConstraint);
      item.computeMinMaxSizes(mainSize, isVertical);
    }

    FlexHypotheticalSizeDetermination.determineBaseAndHypotheticalSizes(items, mainSize, crossSize, isVertical);
    float mainGap = mainGap(isVertical, mainSize);
    List<FlexLine> lines = collectFlexItemsIntoFlexLines(mainSize, items, mainGap);
    for (FlexLine line: lines) {
      Flexer.flex(mainSize, line, mainGap);
    }
    if (!widthConstraint.isPreLayoutConstraint()) {
      FlexCrossSizeDetermination.determineCrossSize(rootBox, lines, crossSize, isVertical);

      alignMainAxis(flexDirection, isVertical, mainSize, lines);
      alignCrossAxis(isVertical, mainSize, crossSize, lines);

      collectChildFragments(items);
    }

    UnmanagedBoxFragment rootFragment = createRootFragment(isVertical, mainSize, crossSize, lines);
    rootBox.updatePositioningFragment(rootFragment);
    return rootFragment;
  }

  private List<FlexItem> collectFlexItems() {
    List<FlexItem> items = new ArrayList<>();
    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      // Anonymous box generation is handled during fixupChildren
      ElementBox childBox = (ElementBox) childIt.next();
      if (!PositionUtil.affectsLayout(childBox)) continue;
      items.add(new FlexItem(childBox));
    }

    return items;
  }

  private int orderOf(Box box) {
    assert box instanceof ElementBox;
    return ((OrderValue) ((ElementBox) box).activeStyles().getProperty(CSSProperty.ORDER)).order();
  }

  private List<FlexLine> collectFlexItemsIntoFlexLines(
    LayoutConstraint mainConstraint, List<FlexItem> flexItems, float mainGap
  ) {
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
        lineSize += item.hypotheticalMainSize() + mainGap;
      }
    }

    lines.add(activeLine);
    return lines;
  }

  private void alignMainAxis(
    FlexDirectionValue flexDirection, boolean isVertical, LayoutConstraint mainSize, List<FlexLine> lines
  ) {
    float mainGap = mainGap(isVertical, mainSize);
    JustifyContentValue contentJustification = (JustifyContentValue) rootBox.activeStyles().getProperty(CSSProperty.JUSTIFY_CONTENT);
    boolean isReverse =
      flexDirection.equals(FlexDirectionValue.ROW_REVERSE)
      || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
    FlexMainAlignment.alignMainAxis(
      new MainAlignmentContext(mainSize, isVertical, isReverse, contentJustification, mainGap),
      lines);
  }

  private void alignCrossAxis(
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize, List<FlexLine> lines
  ) {
    float crossGap = crossGap(isVertical, mainSize);
    AlignContentValue alignContent = (AlignContentValue) rootBox.activeStyles().getProperty(CSSProperty.ALIGN_CONTENT);
    FlexCrossAlignment.alignCrossAxis(
      new CrossAlignmentContext(crossSize, isVertical, alignContent, crossGap),
      lines);
  }

  private float mainGap(boolean isVertical, LayoutConstraint mainSize) {
    ActiveStyles parentStyles = rootBox.activeStyles();
    CSSValue mainGapValue = isVertical ?
      parentStyles.getProperty(CSSProperty.ROW_GAP) :
      parentStyles.getProperty(CSSProperty.COLUMN_GAP);
    LayoutConstraint mainGapConstraint = SizingUtil.evaluateBaseSize(
      rootBox.layoutContext(), mainSize, mainGapValue);
    return mainGapConstraint.isBounded() ? mainGapConstraint.value() : 0;
  }

  private float crossGap(boolean isVertical, LayoutConstraint mainSize) {
    return mainGap(!isVertical, mainSize);
  }

  private void collectChildFragments(List<FlexItem> items) {
    UnmanagedBoxFragment lastFragment = null;
    for (FlexItem item: items) {
      // After fixup, flex should only have element children
      if (!PositionUtil.affectsLayout(item.box())) continue;
      UnmanagedBoxFragment boxFragment = item.fragment();
      boxFragment.setNext(null);
      if (lastFragment == null) {
        fragments = lastFragment = boxFragment;
      } else {
        lastFragment = (UnmanagedBoxFragment) IntrusiveList.add(lastFragment, boxFragment);
        lastFragment = (UnmanagedBoxFragment) lastFragment.next();
      }
    }
  }

  private UnmanagedBoxFragment createRootFragment(
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize, List<FlexLine> lines
  ) {
    float mainGap = mainGap(isVertical, mainSize);
    float crossGap = crossGap(isVertical, mainSize);

    float largestLineMain = 0;
    float totalLineCross = 0;
    for (FlexLine line: lines) {
      largestLineMain = Math.max(line.sumHypotheticalMainSizes(mainGap), totalLineCross);
      totalLineCross += line.crossSize();
    }
    totalLineCross += crossGap * (lines.size() - 1);
    
    float resolvedMain = LayoutUtil.constraintOrDim(mainSize, largestLineMain);
    float resolvedCross = LayoutUtil.constraintOrDim(crossSize, totalLineCross);
    return new UnmanagedBoxFragment(
      isVertical ? resolvedCross : resolvedMain,
      isVertical ? resolvedMain : resolvedCross,
      isVertical ? totalLineCross : largestLineMain,
      isVertical ? largestLineMain : totalLineCross,
      rootBox, painter);
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    LayoutFragment rootFragment = rootBox.positioningFragment();
    StackingContext refContext = rootBox.stackingContext();
    float offsetX = layerX + (rootFragment.contentX() - rootFragment.borderX());
    float offsetY = layerY + (rootFragment.contentY() - rootFragment.borderY());

    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      // Once again, fixup should have made everything ElementBox
      ElementBox childBox = (ElementBox) childIt.next();
      if (!PositionUtil.affectsLayout(childBox)) {
        childBox.dimensions().setStaticPosition(layerX, layerY);
        continue;
      }
    }
  
    UnmanagedBoxFragment childFragment = fragments;
    while (childFragment != null) {
      ElementBox childBox = childFragment.box();
      float childX = offsetX + childFragment.borderX();
      float childY = offsetY + childFragment.borderY();
      if (childBox.stackingContext() != refContext) {
        refContext = childBox.stackingContext();
        refContext.addFragment(childX, childY, childFragment);
      }
      childBox.content().positionLayers(childX, childY);
      childFragment = (UnmanagedBoxFragment) childFragment.next();
    }
  }
  
}
