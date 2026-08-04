package net.buildabrowser.babbrowser.renderer.content.flexbox;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.isBlank;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.content.common.MarginUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexLineCrossAlignment.CrossAlignmentContext;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexMainAlignment.MainAlignmentContext;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public final class FlexBoxContent implements BoxContent {

  private static final FlexBoxContent INSTANCE = new FlexBoxContent();

  private FlexBoxContent() {}

  @Override
  public void fixupChildren(ElementBox rootBox) {
    ElementBoxIterator childIt = rootBox.childBoxes();
    ElementBox anonymousBox = null;
    boolean isOnlyWhitespace = true;
    boolean didInsertBox = false;
    while (childIt.hasNext()) {
      switch (childIt.next()) {
        case ElementBox _1 -> {
          anonymousBox = null;
          didInsertBox = false;
        }
        case TextBox textBox -> {
          if (anonymousBox == null) {
            isOnlyWhitespace = true;
            // It's actually flex-level, but this flag has no effect regardless
            anonymousBox = ElementBox.createAnonymous(rootBox, BoxLevel.BLOCK_LEVEL);
          }
          isOnlyWhitespace = isOnlyWhitespace && isBlank(textBox.text()); // TODO: Proper HTML whitespace
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
  public FlexBoxFragment layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    List<FlexItem> flexItems = collectFlexItems(rootBox);
    return layoutItems(rootBox, flexItems, widthConstraint, heightConstraint);
  }

  private FlexBoxFragment layoutItems(
    ElementBox rootBox,
    List<FlexItem> items,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FlexDirectionValue flexDirection = (FlexDirectionValue) rootBox.properties().get(CSSProperty.FLEX_DIRECTION);
    boolean isVertical = flexDirection.equals(FlexDirectionValue.COLUMN) || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
    LayoutConstraint mainSize = isVertical ? heightConstraint : widthConstraint;
    LayoutConstraint crossSize = isVertical ? widthConstraint : heightConstraint;

    for (FlexItem item: items) {
      MarginUtil.computeSimpleMargin(item.box(), widthConstraint);
      item.box().content().computeMeasures(item.box(), widthConstraint);
      item.computeMinMaxSizes(mainSize, isVertical);
    }

    FlexHypotheticalSizeDetermination.determineBaseAndHypotheticalSizes(
      rootBox, items, mainSize, crossSize, isVertical);
    float mainGap = mainGap(rootBox, isVertical, mainSize);
    List<FlexLine> lines = collectFlexItemsIntoFlexLines(rootBox, mainSize, items, mainGap);
    for (FlexLine line: lines) {
      Flexer.flex(mainSize, line, mainGap);
    }

    UnmanagedBoxFragment<?> fragments = null;
    FlexCrossSizeDetermination.determineCrossSize(rootBox, lines, crossSize, isVertical);
    if (!widthConstraint.isPreLayoutConstraint()) {
      alignMainAxis(rootBox, flexDirection, isVertical, mainSize, lines);
      alignCrossAxis(rootBox, isVertical, mainSize, crossSize, lines);

      fragments = collectChildFragments(items);
    }

    return createRootFragment(
      rootBox, items, isVertical, mainSize, crossSize, lines, fragments);
  }

  private List<FlexItem> collectFlexItems(ElementBox rootBox) {
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
    return ((OrderValue) ((ElementBox) box).properties().get(CSSProperty.ORDER)).order();
  }

  private List<FlexLine> collectFlexItemsIntoFlexLines(
    ElementBox rootBox,
    LayoutConstraint mainConstraint, List<FlexItem> flexItems, float mainGap
  ) {
    List<FlexLine> lines = new LinkedList<>();
    FlexLine activeLine = new FlexLine();
    if (rootBox.properties().get(CSSProperty.FLEX_WRAP).equals(FlexWrapValue.NOWRAP)) {
      for (FlexItem item: flexItems) {
        activeLine.addItem(item);
      }
    } else {
      float lineSize = 0;
      for (FlexItem item: flexItems) {
        if (
          !activeLine.isEmpty()
          && (
            mainConstraint.isBounded()
            || mainConstraint.type().equals(LayoutConstraintType.MIN_CONTENT))
          && lineSize + item.outerSize(item.hypotheticalMainSize()) > mainConstraint.value()
        ) {
          lines.add(activeLine);
          lineSize = 0;
          activeLine = new FlexLine();
        }

        activeLine.addItem(item);
        lineSize += item.outerSize(item.hypotheticalMainSize()) + mainGap;
      }
    }

    lines.add(activeLine);
    return lines;
  }

  private void alignMainAxis(
    ElementBox rootBox,
    FlexDirectionValue flexDirection, boolean isVertical, LayoutConstraint mainSize, List<FlexLine> lines
  ) {
    float mainGap = mainGap(rootBox, isVertical, mainSize);
    JustifyContentValue contentJustification = (JustifyContentValue) rootBox.properties().get(CSSProperty.JUSTIFY_CONTENT);
    if (!mainSize.isBounded()) contentJustification = JustifyContentValue.FLEX_START;
    boolean isReverse =
      flexDirection.equals(FlexDirectionValue.ROW_REVERSE)
      || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
    FlexMainAlignment.alignMainAxis(
      new MainAlignmentContext(mainSize, isVertical, isReverse, contentJustification, mainGap),
      lines);
  }

  private void alignCrossAxis(
    ElementBox rootBox,
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize, List<FlexLine> lines
  ) {
    float crossGap = crossGap(rootBox, isVertical, mainSize);
    AlignItemsValue alignItems = (AlignItemsValue) rootBox.properties().get(CSSProperty.ALIGN_ITEMS);
    AlignContentValue alignContent = (AlignContentValue) rootBox.properties().get(CSSProperty.ALIGN_CONTENT);
    if (!crossSize.isBounded()) alignContent = AlignContentValue.FLEX_START;

    CrossAlignmentContext alignmentContext = new CrossAlignmentContext(
      crossSize, isVertical, alignItems, alignContent, crossGap);
    FlexItemCrossAlignment.alignItems(alignmentContext, lines);
    FlexLineCrossAlignment.alignLines(alignmentContext, lines);
  }

  private float mainGap(
    ElementBox rootBox,
    boolean isVertical, LayoutConstraint mainSize
  ) {
    PropertyContainer parentProperties = rootBox.properties();
    CSSValue mainGapValue = isVertical ?
      parentProperties.get(CSSProperty.ROW_GAP) :
      parentProperties.get(CSSProperty.COLUMN_GAP);
    LayoutConstraint mainGapConstraint = SizingUtil.evaluateBaseSize(
      rootBox.layoutContext(), mainSize, mainGapValue);
    return mainGapConstraint.isBounded() ? mainGapConstraint.value() : 0;
  }

  private float crossGap(
    ElementBox rootBox,
    boolean isVertical, LayoutConstraint mainSize
  ) {
    return mainGap(rootBox, !isVertical, mainSize);
  }

  private UnmanagedBoxFragment<?> collectChildFragments(List<FlexItem> items) {
    UnmanagedBoxFragment<?> fragments = null;
    UnmanagedBoxFragment<?> lastFragment = null;
    for (FlexItem item: items) {
      // After fixup, flex should only have element children
      if (!PositionUtil.affectsLayout(item.box())) continue;
      UnmanagedBoxFragment<?> boxFragment = item.fragment();
      boxFragment.setNext(null);
      if (lastFragment == null) {
        fragments = lastFragment = boxFragment;
      } else {
        lastFragment = (UnmanagedBoxFragment<?>) IntrusiveList.add(lastFragment, boxFragment);
        lastFragment = (UnmanagedBoxFragment<?>) lastFragment.next();
      }
    }

    return fragments;
  }

  private FlexBoxFragment createRootFragment(
    ElementBox rootBox, List<FlexItem> items,
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize,
    List<FlexLine> lines, UnmanagedBoxFragment<?> childFragments
  ) {
    float mainGap = mainGap(rootBox, isVertical, mainSize);
    float crossGap = crossGap(rootBox, isVertical, mainSize);

    float largestLineMain = 0;
    float totalLineCross = 0;
    for (FlexLine line: lines) {
      largestLineMain = Math.max(line.sumHypotheticalMainSizes(mainGap), largestLineMain);
      totalLineCross += line.crossSize();
    }
    totalLineCross += crossGap * (lines.size() - 1);
    
    float resolvedMain = LayoutUtil.clampedUsedWidth(
      rootBox, mainSize, largestLineMain);
    float resolvedCross = LayoutUtil.clampedUsedHeight(
      rootBox, crossSize, totalLineCross);

    // TODO: Properly compute baselines during pre-layout
    boolean skippedLayout = items.size() == 0 || items.get(0).fragment() == null;
    float firstBaseline = !skippedLayout ?
      items.get(0).fragment().firstBaseline(Measurement.MARGIN) : 0;
    float lastBaseline = !skippedLayout ?
      items.get(items.size() - 1).fragment().lastBaseline(Measurement.MARGIN) : 0;
    
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createFlexBoxFragment(
      isVertical ? resolvedCross : resolvedMain,
      isVertical ? resolvedMain : resolvedCross,
      isVertical ? totalLineCross : largestLineMain,
      isVertical ? largestLineMain : totalLineCross,
      firstBaseline, lastBaseline,
      rootBox, childFragments);
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    FlexBoxFragment rootFragment = (FlexBoxFragment) fragment;
    ElementBox rootBox = rootFragment.box();
    rootFragment.setLayerPos(layerX, layerY);

    StackingContext refContext = rootBox.stackingContext();

    float offsetX = layerX + (rootFragment.posX(Measurement.CONTENT) - rootFragment.posX(Measurement.BORDER));
    float offsetY = layerY + (rootFragment.posY(Measurement.CONTENT) - rootFragment.posY(Measurement.BORDER));

    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      // Once again, fixup should have made everything ElementBox
      ElementBox childBox = (ElementBox) childIt.next();
      if (!PositionUtil.affectsLayout(childBox)) {
        childBox.alterDimensions(false, d -> d.setStaticPosition(layerX, layerY));
        continue;
      }
    }
  
    UnmanagedBoxFragment<?> childFragment = rootFragment.fragments();
    while (childFragment != null) {
      ElementBox childBox = childFragment.box();
      float childX = offsetX + childFragment.posX(Measurement.BORDER);
      float childY = offsetY + childFragment.posY(Measurement.BORDER);
      
      if (childBox.stackingContext() != refContext) {
        childBox.stackingContext().positionFragment(
          childX, childY, childFragment,
          childBox.content()::positionLayers);
      } else {
        childFragment.setLayerPos(childX, childY);
        childBox.content().positionLayers(childFragment, childX, childY);
      }

      childFragment = (UnmanagedBoxFragment<?>) childFragment.next();
    }
  }
  
  public static FlexBoxContent get() {
    return INSTANCE;
  }

}
