package net.buildabrowser.babbrowser.renderer.content.table;

import static net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil.outerDisplayValue;

import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public final class TableBoxUtil {
  
  private TableBoxUtil() {}

  // TODO: Might be better to merge the logic, as to not query innerDisplayValue and
  // outerDisplayValue multiple times

  public static boolean isTableNonRoot(ElementBox elementBox) {
    return
      isProperTableChild(elementBox)
      || outerDisplayValue(elementBox.properties()).equals(OuterDisplayValue.TABLE_CELL);
  }

  public static boolean isProperTableChild(ElementBox elementBox) {
    return
      isTableTrackGroup(elementBox)
      || isTableTrack(elementBox)
      || outerDisplayValue(elementBox.properties())
        .equals(OuterDisplayValue.TABLE_CAPTION);
  }

  public static boolean isProperTableChild(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    return isProperTableChild(elementBox);
  }

  public static boolean isColumnGroup(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return displayValue.equals(OuterDisplayValue.TABLE_COLUMN_GROUP);
  }

  public static boolean isTableRow(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return displayValue.equals(OuterDisplayValue.TABLE_ROW);
  }

  public static boolean isTableColumn(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return displayValue.equals(OuterDisplayValue.TABLE_COLUMN);
  }

  public static boolean isTableCell(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return displayValue.equals(OuterDisplayValue.TABLE_CELL);
  }

  public static boolean isTableRowGroup(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return isLikeTableRowGroup(displayValue);
  }

  private static boolean isTableTrack(ElementBox elementBox) {
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return
      displayValue.equals(OuterDisplayValue.TABLE_ROW)
      || displayValue.equals(OuterDisplayValue.TABLE_COLUMN);
  }

  private static boolean isTableTrackGroup(ElementBox elementBox) {
    OuterDisplayValue displayValue = outerDisplayValue(elementBox.properties());
    return
      isLikeTableRowGroup(displayValue)
      || displayValue.equals(OuterDisplayValue.TABLE_COLUMN_GROUP);
  }

  private static boolean isLikeTableRowGroup(OuterDisplayValue displayValue) {
    return
      displayValue.equals(OuterDisplayValue.TABLE_ROW_GROUP)
      || displayValue.equals(OuterDisplayValue.TABLE_HEADER_GROUP)
      || displayValue.equals(OuterDisplayValue.TABLE_FOOTER_GROUP);
  }

}
