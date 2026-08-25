package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowBlockBox;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.test.GridComparator;

public class GridItemPlacerTest {
 
  @Test
  @DisplayName("Can place empty grid")
  public void canPlaceEmptyGrid() {
    
    ElementBox gridBox = flowBlockBox(List.of());
    List<GridItem> items = new ArrayList<>();

    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 1, 1, 1);
    grid.resizeExplicit(gridSpan);
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan, 1);
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place grid with items on areas")
  public void canPlaceGridWithItemsOnAreas() {
    ActiveStyles gridItemBox1Styles = ActiveStyles.create();
    setGridArea(gridItemBox1Styles,
      GridLineValue.create(false, true, 1, "a"));
    ElementBox gridItemBox1 = flowBlockBox(gridItemBox1Styles, List.of());

    ActiveStyles gridItemBox2Styles = ActiveStyles.create();
    setGridArea(gridItemBox2Styles,
      GridLineValue.create(false, true, 1, "b"));
    ElementBox gridItemBox2 = flowBlockBox(gridItemBox2Styles, List.of());

    ActiveStyles gridStyles = ActiveStyles.create();
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_AREAS,
      GridTemplateAreasValue.create(List.of(
        GridArea.create("a", 1, 1, 1, 3),
        GridArea.create("b", 2, 2, 2, 1)
      )));
    ElementBox gridBox = flowBlockBox(gridStyles, List.of());
    List<GridItem> items = new ArrayList<>();
    items.add(GridItem.create(gridItemBox1));
    items.add(GridItem.create(gridItemBox2));

    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 4, 1, 3);
    grid.resizeExplicit(gridSpan);
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan, 1);
    expectedGrid.setElementBox(1, 1, 0, gridItemBox1);
    expectedGrid.setElementBox(1, 2, 0, gridItemBox1);
    expectedGrid.setElementBox(1, 3, 0, gridItemBox1);
    expectedGrid.setElementBox(2, 2, 0, gridItemBox2);
    expectedGrid.setElementBox(3, 2, 0, gridItemBox2);
    
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place grid with item at specified lines")
  public void canPlaceGridWithItemAtLines() {

    ActiveStyles gridItemBoxStyles = ActiveStyles.create();
    // A named line
    gridItemBoxStyles.setProperty(CSSProperty.GRID_COLUMN_START, GridLineValue.create(
      false, false, 1, "line"));
    // Last grid line
    gridItemBoxStyles.setProperty(CSSProperty.GRID_COLUMN_END, GridLineValue.create(
      false, false, -1, null));
    // Third last named line
    gridItemBoxStyles.setProperty(CSSProperty.GRID_ROW_START, GridLineValue.create(
      false, false, -3, "line"));
    // Span 2 named lines
    gridItemBoxStyles.setProperty(CSSProperty.GRID_ROW_END, GridLineValue.create(
      true, false, 2, "line"));
    ElementBox gridItemBox = flowBlockBox(gridItemBoxStyles, List.of());

    ElementBox gridBox = flowBlockBox(List.of());
    List<GridItem> items = new ArrayList<>();
    items.add(GridItem.create(gridItemBox));

    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 4, 1, 8);
    grid.resizeExplicit(gridSpan);
    grid.columnLine(2).addNames(List.of("line"));
    grid.rowLine(2).addNames(List.of("line"));
    grid.rowLine(4).addNames(List.of("line"));
    grid.rowLine(6).addNames(List.of("line"));
    grid.rowLine(8).addNames(List.of("line"));
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(gridSpan, 1);
    expectSpan(expectedGrid, gridItemBox, 2, 4, 4, 7);
    
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place sparse grid with items with only one determinate track position")
  public void canSparsePlaceGridWithItemsWithOnlyOneDeterminateTrackPosition() {
    // x . x .
    // a x . .
    // x a a a
    // . a a a
    // . a . .
    ElementBox dummyItemBox1 = dummyItemBox(1, 1, 1, 1);
    ElementBox dummyItemBox2 = dummyItemBox(3, 3, 1, 1);
    ElementBox dummyItemBox3 = dummyItemBox(2, 2, 2, 2);
    ElementBox dummyItemBox4 = dummyItemBox(1, 1, 3, 3);

    ActiveStyles gridItemBoxStyles1 = ActiveStyles.create();
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_COLUMN_START,
      GridLineValue.create(false, false, 2, null));
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(false, false, 5, null));
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_ROW_START,
      GridLineValue.create(true, false, 2, null));
    ElementBox gridItemBox1 = flowBlockBox(gridItemBoxStyles1, List.of());

    ActiveStyles gridItemBoxStyles2 = ActiveStyles.create();
    gridItemBoxStyles2.setProperty(CSSProperty.GRID_COLUMN_START,
      GridLineValue.create(false, false, 1, null));
    gridItemBoxStyles2.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(false, false, 1, null));
    ElementBox gridItemBox2 = flowBlockBox(gridItemBoxStyles2, List.of());

    ActiveStyles gridItemBoxStyles3 = ActiveStyles.create();
    gridItemBoxStyles3.setProperty(CSSProperty.GRID_COLUMN_START,
      GridLineValue.create(false, false, 2, null));
    gridItemBoxStyles3.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(false, false, 2, null));
    ElementBox gridItemBox3 = flowBlockBox(gridItemBoxStyles3, List.of());

    ActiveStyles gridBoxStyles = ActiveStyles.create();
    gridBoxStyles.setProperty(CSSProperty.GRID_AUTO_FLOW,
      GridAutoFlowValue.create(GridAutoFlowDirection.COLUMN, false));
    ElementBox gridBox = flowBlockBox(gridBoxStyles, List.of());
    List<GridItem> items = new ArrayList<>();
    items.add(GridItem.create(dummyItemBox1));
    items.add(GridItem.create(dummyItemBox2));
    items.add(GridItem.create(dummyItemBox3));
    items.add(GridItem.create(dummyItemBox4));
    items.add(GridItem.create(gridItemBox1));
    items.add(GridItem.create(gridItemBox2));
    items.add(GridItem.create(gridItemBox3));


    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 3, 1, 3);
    grid.resizeExplicit(gridSpan); // Will be implicitly resized to 4x5 later
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan,
      GridSpan.create(1, 4, 1, 5), 1);
    expectedGrid.setElementBox(1, 1, 0, dummyItemBox1);
    expectedGrid.setElementBox(3, 1, 0, dummyItemBox2);
    expectedGrid.setElementBox(2, 2, 0, dummyItemBox3);
    expectedGrid.setElementBox(1, 3, 0, dummyItemBox4);
    expectSpan(expectedGrid, gridItemBox1, 2, 4, 3, 4);
    expectedGrid.setElementBox(1, 2, 0, gridItemBox2);
    expectedGrid.setElementBox(2, 5, 0, gridItemBox3);
    
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place dense grid with items with only one determinate track position")
  public void canPlaceDenseGridWithItemsWithOnlyOneDeterminateTrackPosition() {
    // x . x a a
    // a x . a a
    // x . . a a
    ElementBox dummyItemBox1 = dummyItemBox(1, 1, 1, 1);
    ElementBox dummyItemBox2 = dummyItemBox(3, 3, 1, 1);
    ElementBox dummyItemBox3 = dummyItemBox(2, 2, 2, 2);
    ElementBox dummyItemBox4 = dummyItemBox(1, 1, 3, 3);

    ActiveStyles gridItemBoxStyles1 = ActiveStyles.create();
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_ROW_START,
      GridLineValue.create(false, false, 1, null));
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_ROW_END,
      GridLineValue.create(true, false, 3, null));
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(true, false, 2, null));
    ElementBox gridItemBox1 = flowBlockBox(gridItemBoxStyles1, List.of());

    ActiveStyles gridItemBoxStyles2 = ActiveStyles.create();
    gridItemBoxStyles2.setProperty(CSSProperty.GRID_ROW_START,
      GridLineValue.create(false, false, 2, null));
    gridItemBoxStyles2.setProperty(CSSProperty.GRID_ROW_END,
      GridLineValue.create(false, false, 3, null));
    ElementBox gridItemBox2 = flowBlockBox(gridItemBoxStyles2, List.of());

    ActiveStyles gridBoxStyles = ActiveStyles.create();
    gridBoxStyles.setProperty(CSSProperty.GRID_AUTO_FLOW,
      GridAutoFlowValue.create(GridAutoFlowDirection.ROW, true));
    ElementBox gridBox = flowBlockBox(gridBoxStyles, List.of());
    List<GridItem> items = new ArrayList<>();
    items.add(GridItem.create(dummyItemBox1));
    items.add(GridItem.create(dummyItemBox2));
    items.add(GridItem.create(dummyItemBox3));
    items.add(GridItem.create(dummyItemBox4));
    items.add(GridItem.create(gridItemBox1));
    items.add(GridItem.create(gridItemBox2));

    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 3, 1, 3);
    grid.resizeExplicit(gridSpan); // Will be implicitly resized to 4x3 later
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan,
      GridSpan.create(1, 5, 1, 3), 1);
    expectedGrid.setElementBox(1, 1, 0, dummyItemBox1);
    expectedGrid.setElementBox(3, 1, 0, dummyItemBox2);
    expectedGrid.setElementBox(2, 2, 0, dummyItemBox3);
    expectedGrid.setElementBox(1, 3, 0, dummyItemBox4);
    expectSpan(expectedGrid, gridItemBox1, 4, 5, 1, 3);
    expectedGrid.setElementBox(1, 2, 0, gridItemBox2);
    
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place grid with sparse row auto-flow")
  public void canPlaceGridWithSparseRowAutoFlow() {
    // x .
    // a a
    // a a
    // a a
    // a .
    // a .

    ElementBox dummyItemBox1 = dummyItemBox(1, 1, 1, 1);

    ActiveStyles gridItemBoxStyles1 = ActiveStyles.create();
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_COLUMN_START,
      GridLineValue.create(true, false, 2, null));
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_ROW_END,
      GridLineValue.create(true, false, 2, null));
    ElementBox gridItemBox1 = flowBlockBox(gridItemBoxStyles1, List.of());

    ElementBox gridItemBox2 = flowBlockBox(List.of());
    ElementBox gridItemBox3 = flowBlockBox(List.of());
    ElementBox gridItemBox4 = flowBlockBox(List.of());

    ActiveStyles gridItemBoxStyles5 = ActiveStyles.create();
    gridItemBoxStyles5.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(false, false, 2, null));
    ElementBox gridItemBox5 = flowBlockBox(gridItemBoxStyles5, List.of());

    ActiveStyles gridBoxStyles = ActiveStyles.create();
    gridBoxStyles.setProperty(CSSProperty.GRID_AUTO_FLOW,
      GridAutoFlowValue.create(GridAutoFlowDirection.ROW, false));
    ElementBox gridBox = flowBlockBox(gridBoxStyles, List.of());

    List<GridItem> items = new ArrayList<>();
    items.add(GridItem.create(dummyItemBox1));
    items.add(GridItem.create(gridItemBox1));
    items.add(GridItem.create(gridItemBox2));
    items.add(GridItem.create(gridItemBox3));
    items.add(GridItem.create(gridItemBox4));
    items.add(GridItem.create(gridItemBox5));

    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 1, 1, 1);
    grid.resizeExplicit(gridSpan);
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan, new GridSpan(1, 2, 1, 6), 1);
    expectedGrid.setElementBox(1, 1, 0, dummyItemBox1);
    expectSpan(expectedGrid, gridItemBox1, 1, 2, 2, 3);
    expectedGrid.setElementBox(1, 4, 0, gridItemBox2);
    expectedGrid.setElementBox(2, 4, 0, gridItemBox3);
    expectedGrid.setElementBox(1, 5, 0, gridItemBox4);
    expectedGrid.setElementBox(1, 6, 0, gridItemBox5);
    
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place grid with dense column auto-flow")
  public void canPlaceGridWithDenseColumnAutoFlow() {
    // x a a a a
    // a a a a .

    ElementBox dummyItemBox1 = dummyItemBox(1, 1, 1, 1);

    ActiveStyles gridItemBoxStyles1 = ActiveStyles.create();
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_ROW_START,
      GridLineValue.create(true, false, 2, null));
    gridItemBoxStyles1.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(true, false, 2, null));
    ElementBox gridItemBox1 = flowBlockBox(gridItemBoxStyles1, List.of());

    ElementBox gridItemBox2 = flowBlockBox(List.of());
    ElementBox gridItemBox3 = flowBlockBox(List.of());

    ActiveStyles gridItemBoxStyles4 = ActiveStyles.create();
    gridItemBoxStyles4.setProperty(CSSProperty.GRID_ROW_END,
      GridLineValue.create(false, false, 2, null));
    ElementBox gridItemBox4 = flowBlockBox(gridItemBoxStyles4, List.of());

    ElementBox gridItemBox5 = flowBlockBox(List.of());

    ActiveStyles gridBoxStyles = ActiveStyles.create();
    gridBoxStyles.setProperty(CSSProperty.GRID_AUTO_FLOW,
      GridAutoFlowValue.create(GridAutoFlowDirection.COLUMN, true));
    ElementBox gridBox = flowBlockBox(gridBoxStyles, List.of());

    List<GridItem> items = new ArrayList<>();
    items.add(GridItem.create(dummyItemBox1));
    items.add(GridItem.create(gridItemBox1));
    items.add(GridItem.create(gridItemBox2));
    items.add(GridItem.create(gridItemBox3));
    items.add(GridItem.create(gridItemBox4));
    items.add(GridItem.create(gridItemBox5));

    Grid grid = Grid.create(gridBox);
    GridSpan gridSpan = GridSpan.create(1, 1, 1, 1);
    grid.resizeExplicit(gridSpan);
    GridItemPlacer.placeGridElements(grid, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan, new GridSpan(1, 5, 1, 2), 1);
    expectedGrid.setElementBox(1, 1, 0, dummyItemBox1);
    expectSpan(expectedGrid, gridItemBox1, 2, 3, 1, 2);
    expectedGrid.setElementBox(1, 2, 0, gridItemBox2);
    expectedGrid.setElementBox(4, 1, 0, gridItemBox3);
    expectedGrid.setElementBox(5, 1, 0, gridItemBox4);
    expectedGrid.setElementBox(4, 2, 0, gridItemBox5);
    expectedGrid.compare(grid);
  }

  // TODO: Test overlapping items

  private void expectSpan(
    GridComparator expectedGrid,
    ElementBox gridItemBox,
    int colStart, int colEnd,
    int rowStart, int rowEnd
  ) {
    for (int x = colStart; x <= colEnd; x++) {
      for (int y = rowStart; y <= rowEnd; y++) {
        expectedGrid.setElementBox(x, y, 0, gridItemBox);
      }
    }
  }

  private void setGridArea(ActiveStyles styles, GridLineValue value) {
    styles.setProperty(CSSProperty.GRID_COLUMN_START, value);
    styles.setProperty(CSSProperty.GRID_COLUMN_END, value);
    styles.setProperty(CSSProperty.GRID_ROW_START, value);
    styles.setProperty(CSSProperty.GRID_ROW_END, value);
  }

  private ElementBox dummyItemBox(
    int colStart, int colEnd,
    int rowStart, int rowEnd
  ) {
    ActiveStyles styles = ActiveStyles.create();
    styles.setProperty(CSSProperty.GRID_COLUMN_START,
      GridLineValue.create(false, false, colStart, null));
    styles.setProperty(CSSProperty.GRID_COLUMN_END,
      GridLineValue.create(false, false, colEnd + 1, null));
    styles.setProperty(CSSProperty.GRID_ROW_START,
      GridLineValue.create(false, false, rowStart, null));
    styles.setProperty(CSSProperty.GRID_ROW_END,
      GridLineValue.create(false, false, rowEnd + 1, null));
    return flowBlockBox(styles, List.of());
  }

}
