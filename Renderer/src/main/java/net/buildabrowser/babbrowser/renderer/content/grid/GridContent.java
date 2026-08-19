package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.content.common.GenericFlexibleFixup;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class GridContent implements BoxContent {

  @Override
  public void fixupChildren(ElementBox rootBox) {
    GenericFlexibleFixup.fixupChildren(rootBox);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    Grid grid = Grid.create(box);
    List<GridItem> items = collectGridItems(box);
    GridItemPlacer.placeGridElements(grid, items);
    GridTrackSizingAlgorithm.sizeGridTracks(
      grid, items, heightConstraint, GridDirection.COLUMN);
    GridTrackSizingAlgorithm.sizeGridTracks(
      grid, items, heightConstraint, GridDirection.ROW);
    // TODO: Need to handle "sizing of item depends on available space
    // TODO: min-content stuff
    // TODO: Justify content
    
    positionTracks(grid.tracks(GridDirection.COLUMN));
    positionTracks(grid.tracks(GridDirection.ROW));

    for (GridItem item: items) {
      layoutAndPositionItem(grid, item);
    }

    return null;
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    
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

  private void positionTracks(GridTrack[] tracks) {
    float currentPos = 0;
    for (GridTrack track: tracks) {
      track.setPosition(currentPos);

      LayoutConstraint trackSize = track.baseSize();
      assert trackSize.isBounded();
      currentPos += trackSize.value();
    }
  }

  private void layoutAndPositionItem(Grid grid, GridItem item) {
    float itemWidth = itemSize(grid, item, GridDirection.COLUMN);
    float itemHeight = itemSize(grid, item, GridDirection.ROW);
    UnmanagedBoxFragment<?> itemFragment = item.itemBox().layout(
      LayoutConstraint.of(itemWidth),
      LayoutConstraint.of(itemHeight));
    itemFragment.setPos(
      grid.row(item.rowLineStart()).position(),
      grid.column(item.colLineStart()).position() );
    item.setRelatedFragment(itemFragment);
  }

  private float itemSize(Grid grid, GridItem item, GridDirection direction) {
    float itemSize = 0;
    for (
      int i = item.lineStart(direction);
      i < item.lineEnd(direction);
      i++
    ) {
      GridTrack track = grid.track(i, direction);
      LayoutConstraint trackSize = track.baseSize();
      assert trackSize.isBounded();
      itemSize += trackSize.value();
    }

    return itemSize;
  }

}
