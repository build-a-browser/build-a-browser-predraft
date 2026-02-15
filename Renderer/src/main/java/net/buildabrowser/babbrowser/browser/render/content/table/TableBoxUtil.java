package net.buildabrowser.babbrowser.browser.render.content.table;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;

public final class TableBoxUtil {
  
  private TableBoxUtil() {}

  // TODO: Might be better to merge the logic, as to not query innerDisplayValue and
  // outerDisplayValue multiple times

  public static boolean isTableNonRoot(ElementBox elementBox) {
    return
      isProperTableChild(elementBox)
      || elementBox.activeStyles().outerDisplayValue().equals(OuterDisplayValue.TABLE_CELL);
  }

  public static boolean isProperTableChild(ElementBox elementBox) {
    return
      isTableTrackGroup(elementBox)
      || isTableTrack(elementBox)
      || elementBox.activeStyles().outerDisplayValue()
        .equals(OuterDisplayValue.TABLE_CAPTION);
  }

  public static boolean isProperTableChild(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    return isProperTableChild(elementBox);
  }

  public static boolean isColumnGroup(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = elementBox.activeStyles().outerDisplayValue();
    return displayValue.equals(OuterDisplayValue.TABLE_COLUMN_GROUP);
  }

  public static boolean isTableRow(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = elementBox.activeStyles().outerDisplayValue();
    return displayValue.equals(OuterDisplayValue.TABLE_ROW);
  }

  public static boolean isTableCell(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = elementBox.activeStyles().outerDisplayValue();
    return displayValue.equals(OuterDisplayValue.TABLE_CELL);
  }

  public static boolean isTableRowGroup(Box currentElement) {
    if (!(currentElement instanceof ElementBox elementBox)) return false;
    OuterDisplayValue displayValue = elementBox.activeStyles().outerDisplayValue();
    return displayValue.equals(OuterDisplayValue.TABLE_ROW_GROUP);
  }

  private static boolean isTableTrack(ElementBox elementBox) {
    OuterDisplayValue outerDisplayValue = elementBox.activeStyles().outerDisplayValue();
    return
      outerDisplayValue.equals(OuterDisplayValue.TABLE_ROW)
      || outerDisplayValue.equals(OuterDisplayValue.TABLE_COLUMN);
  }

  private static boolean isTableTrackGroup(ElementBox elementBox) {
    OuterDisplayValue outerDisplayValue = elementBox.activeStyles().outerDisplayValue();
    return
      outerDisplayValue.equals(OuterDisplayValue.TABLE_ROW_GROUP)
      || outerDisplayValue.equals(OuterDisplayValue.TABLE_COLUMN_GROUP);
  }

}
