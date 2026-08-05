package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.flowBlockBox;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.test.GridComparator;

public class GridItemPlacerTest {
 
  @Test
  @DisplayName("Can place empty grid")
  public void canPlaceEmptyGrid() {
    Grid grid = Grid.create();
    GridSpan gridSpan = GridSpan.create(1, 1, 1, 1);
    grid.resizeExplicit(gridSpan);
    
    ElementBox gridBox = flowBlockBox(List.of());
    List<GridItem> items = new ArrayList<>();
    GridItemPlacer.placeGridElements(grid, gridBox, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan, 1);
    expectedGrid.compare(grid);
  }

  @Test
  @DisplayName("Can place grid with items on areas")
  public void canPlaceGridWithItemsOnAreas() {
    Grid grid = Grid.create();
    GridSpan gridSpan = GridSpan.create(1, 4, 1, 3);
    grid.resizeExplicit(gridSpan);

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
    GridItemPlacer.placeGridElements(grid, gridBox, items);

    GridComparator expectedGrid = new GridComparator(
      gridSpan, 1);
    expectedGrid.setElementBox(1, 1, 0, gridItemBox1);
    expectedGrid.setElementBox(1, 2, 0, gridItemBox1);
    expectedGrid.setElementBox(1, 3, 0, gridItemBox1);
    expectedGrid.setElementBox(2, 2, 0, gridItemBox2);
    expectedGrid.setElementBox(3, 2, 0, gridItemBox2);
    
    expectedGrid.compare(grid);
  }

  private void setGridArea(ActiveStyles styles, GridLineValue value) {
    styles.setProperty(CSSProperty.GRID_ROW_START, value);
    styles.setProperty(CSSProperty.GRID_ROW_END, value);
    styles.setProperty(CSSProperty.GRID_COLUMN_START, value);
    styles.setProperty(CSSProperty.GRID_COLUMN_END, value);
  }

}
