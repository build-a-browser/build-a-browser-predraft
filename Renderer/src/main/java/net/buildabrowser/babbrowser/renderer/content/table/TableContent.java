package net.buildabrowser.babbrowser.renderer.content.table;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderCollapseValue;
import net.buildabrowser.babbrowser.cssbase.property.table.BorderSpacingValue;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.table.TableFormer.TableFormingResult;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssigner;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableSeparateBorderAssigner;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public final class TableContent implements BoxContent {

  private static final TableContent INSTANCE = new TableContent();

  private TableContent() {}
  
  @Override
  public void fixupChildren(ElementBox rootBox) {
    TableFixup.adjustTableBox(rootBox);
  }

  // Done during layout
  @Override
  public void computeMeasures(ElementBox box, LayoutConstraint referenceConstraint) {}

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();

    BorderSpacings borderSpacings = determineBorderSpacing(rootBox);
    Table table = Table.create(rootBox, borderSpacings);
    TableFormingResult formingResult = TableFormer.formTable(table, rootBox);
    table.createTracks();

    TableCellUtil.forEachCell(table, cell -> PaddingUtil.computePadding(cell.cellBox(), widthConstraint));
    TableBorderAssignment borderAssignment = assignBorders(table, widthConstraint);

    if (table.width() == 0) {
      TableBoxFragment tableFragment = fragmentFactory.createTableBoxFragment(
        LayoutUtil.clampedUsedWidth(rootBox, widthConstraint, 0),
        LayoutUtil.clampedUsedHeight(rootBox, heightConstraint, 0),
        0, 0,
        0, 0,
        rootBox, table, borderAssignment,
        formingResult.outOfTableFragments());
      rootBox.updatePositioningFragment(tableFragment);
      return tableFragment;
    }

    // TODO: Need to merge unspecified columns with only spans

    List<TableColumn> columns = table.columns();
    List<TableRow> rows = table.rows();
    float hSpaceTotal = (columns.size() + 1) * borderSpacings.hSpace();
    float vSpaceTotal = (rows.size() + 1) * borderSpacings.vSpace();

    float gridMin = TableSizeUtil.sumMinWidths(columns) + hSpaceTotal;
    float gridMax = TableSizeUtil.sumMaxWidths(columns) + hSpaceTotal;

    if (widthConstraint.isPreLayoutConstraint()) {
      float usedWidth = widthConstraint.type().equals(LayoutConstraintType.MAX_CONTENT) ?
        gridMax : gridMin;
      return fragmentFactory.createGenericUnmanagedBoxFragment(
        LayoutUtil.clampedUsedWidth(rootBox, widthConstraint, usedWidth),
        LayoutUtil.clampedUsedHeight(rootBox, heightConstraint, 0),
        usedWidth, 0,
        0, 0, // TODO: Compute baselines
        rootBox);
    }

    LayoutConstraint usedConstraint = widthConstraint.value() < gridMin ?
      LayoutConstraint.of(gridMin) :
      widthConstraint;

    TableColumnSizerAuto.assignTableWidths(
      usedConstraint, columns, borderSpacings);

    layoutCellsAndHeights(table);
    // TODO: Respect alignments and explicit row heights
    positionCells(table);

    float totalHeight = TableSizeUtil.sumHeights(rows) + vSpaceTotal;
    float inkWidth = usedConstraint.isBounded() ?
      Math.max(usedConstraint.floatValue(), gridMin) :
      gridMax;

    positionTracksAndTrackGroups(table, inkWidth, totalHeight);

    TableBoxFragment tableFragment = fragmentFactory.createTableBoxFragment(
      LayoutUtil.clampedUsedWidth(rootBox, usedConstraint, gridMax),
      LayoutUtil.clampedUsedHeight(rootBox, heightConstraint, totalHeight),
      inkWidth, totalHeight,
      0, 0, // TODO: Compute baselines
      rootBox, table, borderAssignment,
      formingResult.outOfTableFragments());
    rootBox.updatePositioningFragment(tableFragment);
    return tableFragment;
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    TablePositioner.positionLayers(
      layerX, layerY,
      (TableBoxFragment) fragment);
  }

  private TableBorderAssignment assignBorders(
    Table table,
    LayoutConstraint widthConstraint
  ) {
    PropertyContainer properties = table.tableBox().properties();
    CSSValue collapseValue = properties.get(CSSProperty.BORDER_COLLAPSE);
    TableBorderAssignment borderAssignment = collapseValue.equals(BorderCollapseValue.COLLAPSE) ?
      TableCollapsedBorderAssigner.assignBorders(table) :
      TableSeparateBorderAssigner.assignBorders(table, widthConstraint);
    return borderAssignment;
  }

  private void layoutCellsAndHeights(Table table) {
    for (int y = 0; y < table.height(); y++) {
      float rowHeight = 0;
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() == null) {
            UnmanagedBoxFragment<?> fragment = cell.cellBox().layout(
              LayoutConstraint.of(innerCellWidth(table, cell)), LayoutConstraint.AUTO);
            cell.setRelatedFragment(fragment);
          }

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

  private List<UnmanagedBoxFragment<?>> positionTracksAndTrackGroups(
    Table table, float tableWidth, float tableHeight
  ) {
    List<UnmanagedBoxFragment<?>> fragments = new ArrayList<>();
    GlobalLayoutContext globalLayoutContext = table.tableBox().layoutContext().global();
    FragmentFactory fragmentFactory = globalLayoutContext.fragmentFactory();

    BorderSpacings borderSpacings = table.spacings();
    float currentX = 0;
    for (int x = 0; x < table.width(); x++) {
      currentX += borderSpacings.hSpace();
      TableColumn column = table.column(x);

      // TODO: Also need to update groups
      ElementBox columnBox = column.columnBox();
      UnmanagedBoxFragment<?> posFragment = fragmentFactory.createGenericUnmanagedBox(
        column.usedWidth(), tableHeight, columnBox);
      posFragment.setPos(currentX, 0);
      columnBox.updatePositioningFragment(posFragment);
      currentX += column.usedWidth();
    }

    float currentY = 0;
    for (int y = 0; y < table.height(); y++) {
      currentY += borderSpacings.vSpace();
      TableRow row = table.row(y);

      // TODO: Also need to update groups
      ElementBox rowBox = row.rowBox();
      UnmanagedBoxFragment<?> posFragment = fragmentFactory.createGenericUnmanagedBox(
        tableWidth, row.usedHeight(), rowBox);
      posFragment.setPos(0, currentY);
      rowBox.updatePositioningFragment(posFragment);
      currentY += row.usedHeight();
    }

    return fragments;
  }

  private List<UnmanagedBoxFragment<?>> positionCells(Table table) {
    List<UnmanagedBoxFragment<?>> fragments = new ArrayList<>();

    BorderSpacings borderSpacings = table.spacings();
    float currentX = 0;
    for (int x = 0; x < table.width(); x++) {
      currentX += borderSpacings.hSpace();

      float currentY = 0;
      for (int y = 0; y < table.height(); y++) {
        currentY += borderSpacings.vSpace();

        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() == null) continue;
          if (cell.cellX() != x || cell.cellY() != y) continue;
          
          cell.getRelatedFragment().setPos(currentX, currentY);
        }
        currentY += table.row(y).usedHeight();
      }
      currentX += table.column(x).usedWidth();
    }

    return fragments;
  }

  private BorderSpacings determineBorderSpacing(ElementBox rootBox) {
    CSSValue borderCollapse = rootBox.properties().get(CSSProperty.BORDER_COLLAPSE);
    if (!(borderCollapse.equals(BorderCollapseValue.SEPARATE))) return BorderSpacings.ZERO;

    CSSValue borderSpacingValue = rootBox.properties().get(CSSProperty.BORDER_SPACING);
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

  public static TableContent get() {
    return INSTANCE;
  }
  
}
