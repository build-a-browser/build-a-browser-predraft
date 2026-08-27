package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignItemsValue;
import net.buildabrowser.babbrowser.cssbase.property.align.JustifyContentValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.content.common.MarginUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericAlignContentAligner;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericAlignContentAligner.CrossAlignmentContext;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericAlignItemAligner;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericFlexibleUtil;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericItem;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentAligner;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentAligner.MainAlignmentContext;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentItem;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericTrack;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public final class FlexBoxContent implements BoxContent {

  private static final FlexBoxContent INSTANCE = new FlexBoxContent();

  private FlexBoxContent() {}

  @Override
  public void fixupChildren(ElementBox rootBox) {
    GenericFlexibleUtil.fixupChildren(rootBox);
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

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    GenericFlexibleUtil.positionLayers(fragment, layerX, layerY);
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
      item.computeMinMaxSizes(mainSize, crossSize, isVertical);
    }

    FlexHypotheticalSizeDetermination.determineBaseAndHypotheticalSizes(
      rootBox, items, mainSize, crossSize, isVertical);
    float mainGap = GenericFlexibleUtil.mainGap(rootBox, isVertical, mainSize);
    List<FlexLine> lines = collectFlexItemsIntoFlexLines(
      rootBox, isVertical, mainSize, items, mainGap);
    
    if (mainSize.isPreLayoutConstraint() || crossSize.isPreLayoutConstraint()) {
      boolean isMinContent = mainSize.type().equals(LayoutConstraintType.MIN_CONTENT);
      mainSize = LayoutConstraint.of(
        FlexMainIntrinsicSizing.determineWebCompatibleSize(
          crossSize, items, isMinContent, lines.size() > 1));
    } else {
      for (FlexLine line: lines) {
        Flexer.flex(mainSize, line, mainGap);
      }
    }

    UnmanagedBoxFragment<?> fragments = null;
    FlexCrossSizeDetermination.determineCrossSize(rootBox, lines, crossSize, isVertical);
    if (!widthConstraint.isPreLayoutConstraint()) {
      @SuppressWarnings({ "rawtypes", "unchecked" })
      List<GenericTrack> linesGeneric = (List<GenericTrack>) (List) lines;
      
      alignMainAxis(rootBox, flexDirection, isVertical, mainSize, lines);
      alignCrossAxis(rootBox, isVertical, mainSize, crossSize, linesGeneric);

      @SuppressWarnings({ "rawtypes", "unchecked" })
      UnmanagedBoxFragment<?> fragments_ = GenericFlexibleUtil.collectChildFragments((List<GenericItem>) (List) items);
      fragments = fragments_;
    }

    return createRootFragment(
      rootBox, items, isVertical,
      widthConstraint, heightConstraint,
      lines, fragments);
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

  private List<FlexLine> collectFlexItemsIntoFlexLines(
    ElementBox rootBox, boolean isVertical,
    LayoutConstraint mainConstraint, List<FlexItem> flexItems, float mainGap
  ) {
    List<FlexLine> lines = new LinkedList<>();
    FlexLine activeLine = new FlexLine(isVertical);
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
          && lineSize + item.hypotheticalMainSize() + item.mainMargin()
            > mainConstraint.value()
        ) {
          lines.add(activeLine);
          lineSize = 0;
          activeLine = new FlexLine(isVertical);
        }

        activeLine.addItem(item);
        lineSize += item.hypotheticalMainSize() + item.mainMargin() + mainGap;
      }
    }

    lines.add(activeLine);
    return lines;
  }

  private void alignMainAxis(
    ElementBox rootBox,
    FlexDirectionValue flexDirection, boolean isVertical, LayoutConstraint mainSize, List<FlexLine> lines
  ) {
    float mainGap = GenericFlexibleUtil.mainGap(rootBox, isVertical, mainSize);
    JustifyContentValue contentJustification = (JustifyContentValue) rootBox.properties().get(CSSProperty.JUSTIFY_CONTENT);
    if (!mainSize.isBounded()) contentJustification = JustifyContentValue.FLEX_START;
    boolean isReverse =
      flexDirection.equals(FlexDirectionValue.ROW_REVERSE)
      || flexDirection.equals(FlexDirectionValue.COLUMN_REVERSE);
    for (FlexLine line: lines) {
      @SuppressWarnings({ "rawtypes", "unchecked" })
      List<GenericJustifyContentItem> items
        = (List<GenericJustifyContentItem>) (List) line.genericItems();
      GenericJustifyContentAligner.justifyContents(
        new MainAlignmentContext(
          mainSize, mainGap, isVertical, isReverse,
          contentJustification, JustifyContentValue.FLEX_START),
        items);
    }
  }

  private void alignCrossAxis(
    ElementBox rootBox,
    boolean isVertical, LayoutConstraint mainSize, LayoutConstraint crossSize, List<GenericTrack> lines
  ) {
    float crossGap = GenericFlexibleUtil.crossGap(rootBox, isVertical, mainSize);
    AlignItemsValue alignItems = (AlignItemsValue) rootBox.properties().get(CSSProperty.ALIGN_ITEMS);
    AlignContentValue alignContent = (AlignContentValue) rootBox.properties().get(CSSProperty.ALIGN_CONTENT);
    if (!crossSize.isBounded()) alignContent = AlignContentValue.FLEX_START;

    CrossAlignmentContext alignmentContext = new CrossAlignmentContext(
      crossSize, crossGap, isVertical, alignItems, alignContent);
    GenericAlignItemAligner.alignItems(alignmentContext, lines);
    GenericAlignContentAligner.alignLines(alignmentContext, lines);
  }

  private FlexBoxFragment createRootFragment(
    ElementBox rootBox, List<FlexItem> items, boolean isVertical,
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint,
    List<FlexLine> lines, UnmanagedBoxFragment<?> childFragments
  ) {
    LayoutConstraint mainSize = isVertical ? heightConstraint : widthConstraint;
    float mainGap = GenericFlexibleUtil.mainGap(rootBox, isVertical, mainSize);
    float crossGap = GenericFlexibleUtil.crossGap(rootBox, isVertical, mainSize);

    float largestLineMain = 0;
    float totalLineCross = 0;
    for (FlexLine line: lines) {
      largestLineMain = Math.max(line.sumHypotheticalMainSizes(mainGap), largestLineMain);
      totalLineCross += line.crossSize();
    }
    totalLineCross += crossGap * (lines.size() - 1);
    
    float targetWidth = isVertical ? totalLineCross : largestLineMain;
    float targetHeight = isVertical ? largestLineMain : totalLineCross;

    float resolvedWidth = LayoutUtil.clampedUsedWidth(
      rootBox, widthConstraint, targetWidth);
    float resolvedHeight = LayoutUtil.clampedUsedHeight(
      rootBox, heightConstraint, targetHeight);

    // TODO: Properly compute baselines during pre-layout
    boolean skippedLayout = items.size() == 0 || items.get(0).fragment() == null;
    float firstBaseline = !skippedLayout ?
      items.get(0).fragment().firstBaseline(Measurement.MARGIN) : 0;
    float lastBaseline = !skippedLayout ?
      items.get(items.size() - 1).fragment().lastBaseline(Measurement.MARGIN) : 0;

    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createFlexBoxFragment(
      resolvedWidth, resolvedHeight,
      targetWidth, targetHeight,
      firstBaseline, lastBaseline,
      rootBox, childFragments);
  }

  public static FlexBoxContent get() {
    return INSTANCE;
  }

}
