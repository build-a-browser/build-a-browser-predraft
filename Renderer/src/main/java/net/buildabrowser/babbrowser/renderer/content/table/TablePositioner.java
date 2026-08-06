package net.buildabrowser.babbrowser.renderer.content.table;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public final class TablePositioner {

  // TODO: Also need to handle things out of flow
  private TablePositioner() {}

  public static void positionLayers(
    float layerX, float layerY,
    TableBoxFragment fragment
  ) {
    StackingContext refContext = fragment.box().stackingContext();
    fragment.setLayerPos(layerX, layerY);

    float offsetX = layerX + (fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER));
    float offsetY = layerY + (fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER));
    TableCellUtil.forEachCell(fragment.table(), cell ->
      positionCell(offsetX, offsetY, refContext, cell));
    for (PosRefBoxFragment posRefBoxFragment: fragment.outOfTableFragments()) {
      posRefBoxFragment.box().alterDimensions(false,
        d -> d.setStaticPosition(offsetX, offsetY));
    }
  }

  private static void positionCell(
    float layerX, float layerY,
    StackingContext refContext,
    TableCell cell
  ) {
    ElementBox box = cell.cellBox();
    UnmanagedBoxFragment<?> boxFragment = cell.getRelatedFragment();
    if (boxFragment == null) return;

    layerX += boxFragment.posX(Measurement.BORDER);
    layerY += boxFragment.posY(Measurement.BORDER);

    if (box.stackingContext() != refContext) {
      float layerX_ = layerX, layerY_ = layerY;
      box.alterDimensions(false, d -> d.setStaticPosition(layerX_, layerY_));
      box.stackingContext().positionFragment(
        layerX, layerY, boxFragment,
        box.content()::positionLayers);
    } else {
      boxFragment.setLayerPos(layerX, layerY);
      box.content().positionLayers(
        boxFragment, layerX, layerY);
    }
  }

}
