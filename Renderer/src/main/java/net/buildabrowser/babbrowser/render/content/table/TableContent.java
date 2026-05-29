package net.buildabrowser.babbrowser.render.content.table;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderCollapseValue;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderSpacingValue;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.table.imp.TableSeparateBorderPainter;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;

public class TableContent implements BoxContent {

  // TODO: Don't forget to handle out-of-flow items

  private static final EventHandler EVENT_HANDLER = new TableEventHandler();
  private static final TableBorderPainter SEPARATE_BORDER_PAINTER = new TableSeparateBorderPainter();

  private final TableContentPainter painter = new TableContentPainter(this);
  private final ElementBox rootBox;
  
  private Table table;
  private TableBorderPainter borderPainter;

  public TableContent(ElementBox rootBox) {
    this.rootBox = rootBox;
  }

  @Override
  public void fixupChildren() {
    TableFixup.adjustTableBox(rootBox);
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    BorderSpacings borderSpacings = determineBorderSpacing();
    this.table = Table.create(rootBox, borderSpacings);
    TableFormer.formTable(table, rootBox);
    table.createTracks();

    if (table.width() == 0) {
      return new UnmanagedBoxFragment(
        LayoutUtil.constraintOrDim(widthConstraint, 0),
        LayoutUtil.constraintOrDim(heightConstraint, 0),
        0, 0,
        rootBox, painter);
    }

    // TODO: Need to merge unspecified columns with only spans

    this.borderPainter = SEPARATE_BORDER_PAINTER;
    borderPainter.assignBorders(table, widthConstraint);

    List<TableColumn> columns = table.columns();
    float hSpaceTotal = (columns.size() + 1) * borderSpacings.hSpace();
    float vSpaceTotal = (columns.size() + 1) * borderSpacings.vSpace();

    float gridMin = TableSizeUtil.sumMinWidths(columns) + hSpaceTotal;
    float gridMax = TableSizeUtil.sumMaxWidths(columns) + hSpaceTotal;

    if (widthConstraint.isPreLayoutConstraint()) {
      float usedWidth = widthConstraint.type().equals(LayoutConstraintType.MAX_CONTENT) ?
        gridMax : gridMin;
      return new UnmanagedBoxFragment(
        LayoutUtil.constraintOrDim(widthConstraint, usedWidth),
        LayoutUtil.constraintOrDim(heightConstraint, 0),
        usedWidth, 0,
        rootBox, painter);
    }

    if (widthConstraint.value() < gridMin) {
      widthConstraint = LayoutConstraint.of(gridMin);
    }

    TableColumnSizerAuto.assignTableWidths(
      widthConstraint, columns, borderSpacings);

    layoutCellsAndHeights(table);
    // TODO: Respect alignments and explicit row heights
    positionCells(table);

    float totalHeight = TableSizeUtil.sumHeights(table.rows()) + vSpaceTotal;
    float inkWidth = widthConstraint.isBounded() ?
      Math.max(widthConstraint.floatValue(), gridMin) :
      gridMax;

    positionTracksAndTrackGroups(table, inkWidth, totalHeight);

    return new UnmanagedBoxFragment(
      LayoutUtil.constraintOrDim(widthConstraint, gridMax),
      LayoutUtil.constraintOrDim(heightConstraint, totalHeight),
      inkWidth, totalHeight,
      rootBox, painter);
  }

  @Override
  public EventHandler eventHandler() {
    return EVENT_HANDLER;
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    // TODO: Implement this
  }

  public Table table() {
    return this.table;
  }

  public TableBorderPainter borderPainter() {
    return this.borderPainter;
  }

  private void layoutCellsAndHeights(Table table) {
    for (int y = 0; y < table.height(); y++) {
      float rowHeight = 0;
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() != null) continue;

          UnmanagedBoxFragment fragment = cell.cellBox().layout(
            LayoutConstraint.of(innerCellWidth(table, cell)), LayoutConstraint.AUTO);
          cell.setRelatedFragment(fragment);

          LayoutConstraint fragmentHeight = SizingHeightUtil.evaluateAdjustedHeightSize(
            LayoutConstraint.AUTO, cell.cellBox());

          float innerHeight = fragmentHeight.isBounded() ?
            fragmentHeight.value() :
            cell.getRelatedFragment().height(Measurement.CONTENT);
          // cell.height() > 1 is technically unspecified behaviour
          float usedHeight = outerCellHeight(cell, innerHeight);
          float itemHeight = usedHeight / cell.height();
          
          rowHeight = Math.max(rowHeight, itemHeight);
        }
      }
      table.row(y).setUsedHeight(rowHeight);
    }
  }

  private float innerCellWidth(Table table, TableCell cell) {
    float totalOuterWidth = 0;
    for (int x = cell.cellX(); x < cell.cellX() + cell.width(); x++) {
      totalOuterWidth += table.column(x).usedWidth();
    }

    float[] padding = cell.cellBox().dimensions().getComputedPadding();
    float totalHPadding = padding[2] + padding[3];
    TableComputedBorders borders = cell.borders();
    float totalHBorder = borders.leftBorder.borderWidth() + borders.rightBorder.borderWidth();

    return totalOuterWidth - totalHPadding - totalHBorder;
  }

  private float outerCellHeight(TableCell cell, float innerHeight) {
    float[] padding = cell.cellBox().dimensions().getComputedPadding();
    float totalVPadding = padding[0] + padding[1];
    TableComputedBorders borders = cell.borders();
    float totalVBorder = borders.topBorder.borderWidth() + borders.bottomBorder.borderWidth();

    return innerHeight + totalVPadding + totalVBorder;
  }

  private List<UnmanagedBoxFragment> positionTracksAndTrackGroups(
    Table table, float tableWidth, float tableHeight
  ) {
    List<UnmanagedBoxFragment> fragments = new ArrayList<>();

    BorderSpacings borderSpacings = table.spacings();
    float currentX = 0;
    for (int x = 0; x < table.width(); x++) {
      currentX += borderSpacings.hSpace();
      TableColumn column = table.column(x);

      // TODO: Also need to update groups
      ElementBox columnBox = column.columnBox();
      columnBox.updatePositioningFragment(
        new UnmanagedBoxFragment(currentX, 0, column.usedWidth(), tableHeight, columnBox));
      currentX += column.usedWidth();
    }

    float currentY = 0;
    for (int y = 0; y < table.height(); y++) {
      currentY += borderSpacings.vSpace();
      TableRow row = table.row(y);

      // TODO: Also need to update groups
      ElementBox rowBox = row.rowBox();
      rowBox.updatePositioningFragment(
        new UnmanagedBoxFragment(0, currentY, tableWidth, row.usedHeight(), rowBox));
      currentY += row.usedHeight();
    }

    return fragments;
  }

  private List<UnmanagedBoxFragment> positionCells(Table table) {
    List<UnmanagedBoxFragment> fragments = new ArrayList<>();

    BorderSpacings borderSpacings = table.spacings();
    float currentX = 0;
    for (int x = 0; x < table.width(); x++) {
      currentX += borderSpacings.hSpace();
      TableColumn column = table.column(x);

      float currentY = 0;
      for (int y = 0; y < table.height(); y++) {
        currentY += borderSpacings.vSpace();

        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() == null) continue;
          if (cell.cellX() != x || cell.cellY() != y) continue;
          
          cell.getRelatedFragment().setPos(currentX, currentY);
        }
        currentY += table.rows().get(y).usedHeight();
      }
      currentX += column.usedWidth();
    }

    return fragments;
  }

  private BorderSpacings determineBorderSpacing() {
    CSSValue borderCollapse = rootBox.activeStyles().getProperty(CSSProperty.BORDER_COLLAPSE);
    if (!(borderCollapse.equals(BorderCollapseValue.SEPARATE))) return BorderSpacings.ZERO;

    CSSValue borderSpacingValue = rootBox.activeStyles().getProperty(CSSProperty.BORDER_SPACING);
    if (!(borderSpacingValue instanceof BorderSpacingValue borderSpacingValues)) return BorderSpacings.ZERO;

    LayoutConstraint borderHSize = SizingUtil.evaluateBaseSize(
      rootBox.layoutContext(), LayoutConstraint.AUTO, borderSpacingValues.hSpace());
    if (!borderHSize.isBounded()) return BorderSpacings.ZERO;

    LayoutConstraint borderVSize = SizingUtil.evaluateBaseSize(
      rootBox.layoutContext(), LayoutConstraint.AUTO, borderSpacingValues.vSpace());
    if (!borderVSize.isBounded()) return BorderSpacings.ZERO;

    return new BorderSpacings(borderHSize.value(), borderVSize.value());
  }

  // TODO: Avoid needing this wrapper
  public static record BorderSpacings(float hSpace, float vSpace) {

    public static BorderSpacings ZERO = new BorderSpacings(0, 0);

  }
  
}
