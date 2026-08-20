package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignItemsValue;
import net.buildabrowser.babbrowser.cssbase.property.align.JustifyContentValue;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericAlignContentAligner;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericAlignContentAligner.CrossAlignmentContext;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericFlexibleUtil;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericItem;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentAligner;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentAligner.MainAlignmentContext;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class GridContent implements BoxContent {

  private static final GridContent INSTANCE = new GridContent();

  @Override
  public void fixupChildren(ElementBox rootBox) {
    GenericFlexibleUtil.fixupChildren(rootBox);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    Grid grid = Grid.create(box);
    List<GridItem> items = collectGridItems(box);
    GridSizer.sizeGridAndPlaceLines(
      grid, box.properties(), box.layoutContext(),
      widthConstraint, heightConstraint);
    GridItemPlacer.placeGridElements(grid, items);
    GridTrackSizingAlgorithm.sizeGridTracks(
      grid, items, widthConstraint, GridDirection.COLUMN);
    GridTrackSizingAlgorithm.sizeGridTracks(
      grid, items, heightConstraint, GridDirection.ROW);
    // TODO: Need to handle "sizing of item depends on available space
    // TODO: min-content stuff
    // TODO: Align content
    
    justifyInlineAxis(box, widthConstraint, grid);
    float resolvedInline = trackBounds(grid.tracks(GridDirection.COLUMN));

    alignBlockAxis(box, widthConstraint, heightConstraint, grid);
    float resolvedBlock = trackBounds(grid.tracks(GridDirection.ROW));

    float usedInline = LayoutUtil.constraintOrDim(widthConstraint, resolvedInline);
    float usedBlock = LayoutUtil.constraintOrDim(heightConstraint, resolvedBlock);

    float totalInline = usedInline;
    float totalBlock = usedBlock;
    for (GridItem item: items) {
      if (!PositionUtil.affectsLayout(item.box())) continue;
      layoutAndPositionItem(grid, item);

      UnmanagedBoxFragment<?> itemFragment = item.fragment();
      totalInline = Math.max(totalInline,
        itemFragment.posX(Measurement.BORDER) + itemFragment.width(Measurement.BORDER));
      totalBlock = Math.max(totalBlock,
        itemFragment.posY(Measurement.BORDER) + itemFragment.height(Measurement.BORDER));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    UnmanagedBoxFragment<?> childFragments = GenericFlexibleUtil.collectChildFragments((List<GenericItem>) (List) items);

    // TODO: Copied from Flex. May or may not be correct
    boolean skippedLayout = items.size() == 0 || items.get(0).fragment() == null;
    float firstBaseline = !skippedLayout ?
      items.get(0).fragment().firstBaseline(Measurement.MARGIN) : 0;
    float lastBaseline = !skippedLayout ?
      items.get(items.size() - 1).fragment().lastBaseline(Measurement.MARGIN) : 0;
    
    FragmentFactory fragmentFactory = box.layoutContext().global().fragmentFactory();
    return fragmentFactory.createGridBoxFragment(
      usedInline, usedBlock,
      totalInline, totalBlock,
      firstBaseline, lastBaseline,
      box, childFragments);
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    GenericFlexibleUtil.positionLayers(fragment, layerX, layerY);
  }

  private List<GridItem> collectGridItems(ElementBox rootBox) {
    List<GridItem> items = new ArrayList<>();
    ElementBoxIterator childIt = rootBox.childBoxes();
    while (childIt.hasNext()) {
      // Anonymous box generation is handled during fixupChildren
      ElementBox childBox = (ElementBox) childIt.next();
      if (!PositionUtil.affectsLayout(childBox)) continue;
      items.add(GridItem.create(childBox));
    }

    return items;
  }

  private void justifyInlineAxis(ElementBox box, LayoutConstraint widthConstraint, Grid grid) {
    float mainGap = GenericFlexibleUtil.mainGap(box, false, widthConstraint);
    JustifyContentValue contentJustification = (JustifyContentValue) box.properties().get(CSSProperty.JUSTIFY_CONTENT);
    GenericJustifyContentAligner.justifyContents(
      new MainAlignmentContext(
        widthConstraint, mainGap, false, false,
        contentJustification, JustifyContentValue.STRETCH),
      List.of(grid.tracks(GridDirection.COLUMN)));
  }

  private void alignBlockAxis(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    Grid grid
  ) {
    float crossGap = GenericFlexibleUtil.crossGap(box, false, widthConstraint);
    AlignItemsValue alignItems = (AlignItemsValue) box.properties().get(CSSProperty.ALIGN_ITEMS);
    AlignContentValue alignContent = (AlignContentValue) box.properties().get(CSSProperty.ALIGN_CONTENT);
    GenericAlignContentAligner.alignLines(
      new CrossAlignmentContext(
        heightConstraint, crossGap, false,
        alignItems, alignContent),
      List.of(grid.tracks(GridDirection.ROW)));
  }

  private float trackBounds(GridTrack[] tracks) {
    float trackBounds = 0;
    for (GridTrack track: tracks) {
      LayoutConstraint trackSize = track.baseSize();
      assert trackSize.isBounded();
      float trackBound = track.position() + trackSize.value();
      trackBounds = Math.max(trackBounds, trackBound);
    }

    return trackBounds;
  }

  private void layoutAndPositionItem(Grid grid, GridItem item) {
    GridTrack endCol = grid.column(item.colLineEnd() - 1);
    assert endCol.baseSize().isBounded();
    float startBlockPos = grid.column(item.colLineStart()).position();
    float endBlockPos = endCol.position() + endCol.baseSize().value();
    float itemWidth = endBlockPos - startBlockPos;

    GridTrack endRow = grid.row(item.rowLineEnd() - 1);
    assert endRow.baseSize().isBounded();
    float startInlinePos = grid.row(item.rowLineStart()).position();
    float endInlinePos = endRow.position() + endRow.baseSize().value();
    float itemHeight = endInlinePos - startInlinePos;

    UnmanagedBoxFragment<?> itemFragment = item.box().layout(
      LayoutConstraint.of(itemWidth),
      LayoutConstraint.of(itemHeight));
    itemFragment.setPos(
      startBlockPos, startInlinePos);
    item.setRelatedFragment(itemFragment);
  }
  
  public static GridContent get() {
    return INSTANCE;
  }

}
