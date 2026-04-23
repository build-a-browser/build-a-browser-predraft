package net.buildabrowser.babbrowser.render.content.table;

import static net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil.outerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;

public final class TableBoxUtil {
  
  private TableBoxUtil() {}

  // TODO: Might be better to merge the logic, as to not query innerDisplayValue and
  // outerDisplayValue multiple times

  public static boolean isTableNonRoot(ElementBox elementBox) {
    return
      isProperTableChild(elementBox)
      || outerDisplayValue(elementBox.activeStyles()).equals(OuterDisplayValue.TABLE_CELL);
  }

  public static boolean isProperTableChild(ElementBox elementBox) {
    return
      isTableTrackGroup(elementBox)
      || isTableTrack(elementBox)
      || outerDisplayValue(elementBox.activeStyles())
        .equals(OuterDisplayValue.TABLE_CAPTION);
  }

  public static boolean isProperTableChild(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    return isProperTableChild(elementBox);
  }

  public static boolean isColumnGroup(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.activeStyles());
    return displayValue.equals(OuterDisplayValue.TABLE_COLUMN_GROUP);
  }

  public static boolean isTableRow(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.activeStyles());
    return displayValue.equals(OuterDisplayValue.TABLE_ROW);
  }

  public static boolean isTableCell(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.activeStyles());
    return displayValue.equals(OuterDisplayValue.TABLE_CELL);
  }

  public static boolean isTableRowGroup(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.activeStyles());
    return displayValue.equals(OuterDisplayValue.TABLE_ROW_GROUP);
  }

  private static boolean isTableTrack(ElementBox elementBox) {
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.activeStyles());
    return
      displayValue.equals(OuterDisplayValue.TABLE_ROW)
      || displayValue.equals(OuterDisplayValue.TABLE_COLUMN);
  }

  private static boolean isTableTrackGroup(ElementBox elementBox) {
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.activeStyles());
    return
      displayValue.equals(OuterDisplayValue.TABLE_ROW_GROUP)
      || displayValue.equals(OuterDisplayValue.TABLE_COLUMN_GROUP);
  }

}
