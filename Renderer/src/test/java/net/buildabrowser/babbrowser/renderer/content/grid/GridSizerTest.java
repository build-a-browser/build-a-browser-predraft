package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackListValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNameComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNumberComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.renderer.content.common.test.LayoutContextTestUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public class GridSizerTest {

  private static final GridTrackValue SAMPLE_FIXED_TRACK
    = GridTrackValue.create(List.of(), LengthValue.create(1, LengthType.PX));

  private static final LayoutConstraint WIDTH_CONSTRAINT = LayoutConstraint.of(200);
  private static final LayoutConstraint HEIGHT_CONSTRAINT = LayoutConstraint.of(200);
  private static final LayoutContext LAYOUT_CONTEXT = LayoutContextTestUtil.createTestLayoutContext(
    WIDTH_CONSTRAINT, HEIGHT_CONSTRAINT);
  
  @Test
  @DisplayName("Can size grid with no-repeat tracks")
  public void canSizeGridWithNoRepeatTracks() {
    ActiveStyles gridStyles = ActiveStyles.create();
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_ROWS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK), null));
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_COLUMNS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK, SAMPLE_FIXED_TRACK), null));
    PropertyContainer properties = ActiveStyles.unparentedStyles(gridStyles);

    
    Grid grid = Grid.create();
    GridSizer.sizeGrid(grid, properties, LAYOUT_CONTEXT, WIDTH_CONSTRAINT, HEIGHT_CONSTRAINT);

    GridSpan expected = new GridSpan(1, 1, 1, 2);
    Assertions.assertEquals(expected, grid.explicitSpan());
  }

  @Test
  @DisplayName("Can size grid with repeat tracks")
  public void canSizeGridWithRepeatTracks() {
    ActiveStyles gridStyles = ActiveStyles.create();
    
    GridTrackValue repeatTrack = GridTrackValue.create(List.of(), GridRepeatValue.create(
        GridRepeatNumberComponent.create(4), 
        GridTrackListValue.create(List.of(SAMPLE_FIXED_TRACK), null)));
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_ROWS, GridTrackListValue.create(
      List.of(repeatTrack), null));
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_COLUMNS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK, repeatTrack), null));
    PropertyContainer properties = ActiveStyles.unparentedStyles(gridStyles);

    Grid grid = Grid.create();
    GridSizer.sizeGrid(grid, properties, LAYOUT_CONTEXT, WIDTH_CONSTRAINT, HEIGHT_CONSTRAINT);

    GridSpan expected = new GridSpan(1, 4, 1, 5);
    Assertions.assertEquals(expected, grid.explicitSpan());
  }

    @Test
  @DisplayName("Can size grid with auto-repeat")
  public void canSizeGridWithAutoRepeat() {
    ActiveStyles gridStyles = ActiveStyles.create();
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_ROWS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK), null));
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_COLUMNS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK, SAMPLE_FIXED_TRACK),
      GridRepeatValue.create(
        GridRepeatNameComponent.AUTO_FILL,
        GridTrackListValue.create(
          List.of(
            GridTrackValue.create(List.of(), LengthValue.create(25, LengthType.PX)),
            GridTrackValue.create(List.of(), LengthValue.create(50, LengthType.PX))
          ), null))));
    PropertyContainer properties = ActiveStyles.unparentedStyles(gridStyles);

    
    Grid grid = Grid.create();
    GridSizer.sizeGrid(grid, properties, LAYOUT_CONTEXT, WIDTH_CONSTRAINT, HEIGHT_CONSTRAINT);

    GridSpan expected = new GridSpan(1, 1, 1, 6);
    Assertions.assertEquals(expected, grid.explicitSpan());
  }

  @Test
  @DisplayName("Can size grid with template areas")
  public void canSizeGridWithTemplateAreas() {
    ActiveStyles gridStyles = ActiveStyles.create();
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_ROWS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK), null));
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_COLUMNS, GridTrackListValue.create(
      List.of(SAMPLE_FIXED_TRACK, SAMPLE_FIXED_TRACK), null));
    gridStyles.setProperty(CSSProperty.GRID_TEMPLATE_AREAS, GridTemplateAreasValue.create(List.of(
      GridArea.create("a", 1, 1, 1, 3),
      GridArea.create("b", 2, 1, 1, 3)
    )));
    PropertyContainer properties = ActiveStyles.unparentedStyles(gridStyles);

    
    Grid grid = Grid.create();
    GridSizer.sizeGrid(grid, properties, LAYOUT_CONTEXT, WIDTH_CONSTRAINT, HEIGHT_CONSTRAINT);

    GridSpan expected = new GridSpan(1, 2, 1, 3);
    Assertions.assertEquals(expected, grid.explicitSpan());
  }

}
