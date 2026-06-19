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
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexCrossAlignment.CrossAlignmentContext;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexMainAlignment.MainAlignmentContext;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;

public class FlexBoxContent implements BoxContent {

  private final ElementBox rootBox;

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
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    List<FlexItem> flexItems = collectFlexItems();
    return layoutItems(flexItems, widthConstraint, heightConstraint);
  }

  private FlexBoxFragment layoutItems(
    List<FlexItem> items, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
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

    FlexHypotheticalSizeDetermination.determineBaseAndHypotheticalSizes(items, mainSize, crossSize, isVertical);
    float mainGap = mainGap(isVertical, mainSize);
    List<FlexLine> lines = collectFlexItemsIntoFlexLines(mainSize, items, mainGap);
    for (FlexLine line: lines) {
      Flexer.flex(mainSize, line, mainGap);
    }

    UnmanagedBoxFragment<?> fragments = null;
    if (!widthConstraint.isPreLayoutConstraint()) {
      FlexCrossSizeDetermination.determineCrossSize(rootBox, lines, crossSize, isVertical);

      alignMainAxis(flexDirection, isVertical, mainSize, lines);
      alignCrossAxis(isVertical, mainSize, crossSize, lines);

      fragments = collectChildFragments(items);
    }

    FlexBoxFragment rootFragment = createRootFragment(
      isVertical, mainSize, crossSize, lines, fragments);
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
    return ((OrderValue) ((ElementBox) box).properties().get(CSSProperty.ORDER)).order();
  }

  private List<FlexLine> collectFlexItemsIntoFlexLines(
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
          && !mainConstraint.type().equals(LayoutConstraintType.MAX_CONTENT)
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
    FlexDirectionValue flexDirection, boolean isVertical, LayoutConstraint mainSize, List<FlexLine> lines
  ) {
    float mainGap = mainGap(isVertical, mainSize);
    JustifyContentValue contentJustification = (JustifyContentValue) rootBox.properties().get(CSSProperty.JUSTIFY_CONTENT);
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
    AlignContentValue alignContent = (AlignContentValue) rootBox.properties().get(CSSProperty.ALIGN_CONTENT);
    FlexCrossAlignment.alignCrossAxis(
      new CrossAlignmentContext(crossSize, isVertical, alignContent, crossGap),
      lines);
  }

  private float mainGap(boolean isVertical, LayoutConstraint mainSize) {
    PropertyContainer parentProperties = rootBox.properties();
    CSSValue mainGapValue = isVertical ?
      parentProperties.get(CSSProperty.ROW_GAP) :
      parentProperties.get(CSSProperty.COLUMN_GAP);
    LayoutConstraint mainGapConstraint = SizingUtil.evaluateBaseSize(
      rootBox.layoutContext(), mainSize, mainGapValue);
    return mainGapConstraint.isBounded() ? mainGapConstraint.value() : 0;
  }

  private float crossGap(boolean isVertical, LayoutConstraint mainSize) {
    return mainGap(!isVertical, mainSize);
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
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize,
    List<FlexLine> lines, UnmanagedBoxFragment<?> childFragments
  ) {
    float mainGap = mainGap(isVertical, mainSize);
    float crossGap = crossGap(isVertical, mainSize);

    float largestLineMain = 0;
    float totalLineCross = 0;
    for (FlexLine line: lines) {
      largestLineMain = Math.max(line.sumHypotheticalMainSizes(mainGap), largestLineMain);
      totalLineCross += line.crossSize();
    }
    totalLineCross += crossGap * (lines.size() - 1);
    
    float resolvedMain = LayoutUtil.constraintOrDim(mainSize, largestLineMain);
    float resolvedCross = LayoutUtil.constraintOrDim(crossSize, totalLineCross);

    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createFlexBoxFragment(
      isVertical ? resolvedCross : resolvedMain,
      isVertical ? resolvedMain : resolvedCross,
      isVertical ? totalLineCross : largestLineMain,
      isVertical ? largestLineMain : totalLineCross,
      rootBox, childFragments);
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    FlexBoxFragment rootFragment = (FlexBoxFragment) rootBox.positioningFragment();
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
        childFragment.setLayerPos(layerX, layerY);
        childBox.content().positionLayers(childX, childY);
      }

      childFragment = (UnmanagedBoxFragment<?>) childFragment.next();
    }
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }
  
}
