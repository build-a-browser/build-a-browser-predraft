package net.buildabrowser.babbrowser.renderer.content.table;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;

public final class TablePositioner {

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
  }

  private static void positionCell(
    float layerX, float layerY,
    StackingContext refContext,
    TableCell cell
  ) {
    ElementBox box = cell.cellBox();
    BoxFragment<?> boxFragment = cell.getRelatedFragment();

    layerX += boxFragment.posX(Measurement.BORDER);
    layerY += boxFragment.posY(Measurement.BORDER);

    if (box.stackingContext() != refContext) {
      box.stackingContext().positionFragment(
        layerX, layerY, boxFragment,
        box.content()::positionLayers);
    } else {
      boxFragment.setLayerPos(layerX, layerY);
      box.content().positionLayers(layerX, layerY);
    }
  }

}
