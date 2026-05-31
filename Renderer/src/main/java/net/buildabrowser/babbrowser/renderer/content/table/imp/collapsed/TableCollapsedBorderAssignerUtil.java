package net.buildabrowser.babbrowser.renderer.content.table.imp.collapsed;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.ComputedBorder;

public class TableCollapsedBorderAssignerUtil {

  private static final List<CSSValue> BORDER_STYLE_ORDER = List.of(
    BorderStyleValue.DOUBLE,
    BorderStyleValue.SOLID,
    BorderStyleValue.DASHED,
    BorderStyleValue.DOTTED,
    BorderStyleValue.RIDGE,
    BorderStyleValue.OUTSET,
    BorderStyleValue.GROOVE,
    BorderStyleValue.INSET,
    CSSValue.NONE
  );
  
  private TableCollapsedBorderAssignerUtil() {}

  public static TableComputedBorders computeInitialBorders(
    ElementBox elBox,
    TableComputedBorders borders
  ) {
    borders.topBorder = TableComputedBorders.computeBorder(elBox, BorderSide.TOP, true);
    borders.bottomBorder = TableComputedBorders.computeBorder(elBox, BorderSide.BOTTOM, true);
    borders.leftBorder = TableComputedBorders.computeBorder(elBox, BorderSide.LEFT, true);
    borders.rightBorder = TableComputedBorders.computeBorder(elBox, BorderSide.RIGHT, true);
    return borders;
  }

  public static ComputedBorder strongerBorder(ComputedBorder a, ComputedBorder b) {
    if (a == null) return b;
    if (b == null) return a;
    if (isCurrentMoreSpecific(a, b)) {
      return b;
    } else {
      return a;
    }
  }

  public static boolean isCurrentMoreSpecific(
    ComputedBorder oldBorder, ComputedBorder currentBorder
  ) {
    if (
      oldBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
      && !currentBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
    ) return false;
    if (
      !oldBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
      && currentBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
    ) return true;

    if (
      oldBorder.borderWidth() > currentBorder.borderWidth()
    ) return false;
    if (
      oldBorder.borderWidth() < currentBorder.borderWidth()
    ) return true;

    int oldBorderOrder = BORDER_STYLE_ORDER.indexOf(oldBorder.borderStyle());
    int currentBorderOrder = BORDER_STYLE_ORDER.indexOf(currentBorder.borderStyle());

    if (oldBorderOrder < currentBorderOrder) return false;
    if (oldBorderOrder > currentBorderOrder) return true;

    return false;
  }

  public static int compareCellOrder(TableCell a, TableCell b) {
    return
      a.cellY() < b.cellY() ? -1 :
      a.cellY() > b.cellY() ? 1 :
      a.cellX() < b.cellX() ? -1 :
      a.cellX() > b.cellX() ? 1 :
      0;
  }

}
