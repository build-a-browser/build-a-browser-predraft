package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridTemplateAreasRowValue;

public final class GridTemplateAreasFormer {
  
  private GridTemplateAreasFormer() {}

  public static List<GridArea> formGridAreas(
    List<GridTemplateAreasRowValue> rows
  ) {
    List<GridArea> areas = new ArrayList<>();
    Map<String, GridArea> areasMap = new HashMap<>();

    for (int y = 0; y < rows.size(); y++) {
      GridTemplateAreasRowValue row = rows.get(y);
      for (int x = 0; x < row.cellNames().size(); x++) {
        String cellName = row.cellNames().get(x);
        if (cellName == null) continue;
        GridArea existingArea = areasMap.get(cellName);
        if (
          existingArea != null
          && x + 1 >= existingArea.x()
          && x + 1 < existingArea.x() + existingArea.w()
          && y + 1 >= existingArea.y()
          && y + 1 < existingArea.y() + existingArea.h()
        ) {
          continue;
        } else if (existingArea != null) {
          return null;
        } else {
          GridArea area = createGridArea(rows, x, y, cellName);
          if (area == null) return null;
          areas.add(area);
          areasMap.put(cellName, area);
        }
      }
    }

    return areas;
  }

  private static GridArea createGridArea(
    List<GridTemplateAreasRowValue> rows,
    int x, int y, String targetName
  ) {
    List<String> rowCellNames = rows.get(y).cellNames();
    int w = 1;
    for (int x2 = x + 1; x2 < rowCellNames.size(); x2++) {
      if (!targetName.equals(rowCellNames.get(x2))) break;
      w++;
    }

    int h = 1;
    for (int y2 = y + 1; y2 < rows.size(); y2++) {
      if (!targetName.equals(rows.get(y2).cellNames().get(x))) break;
      h++;
    }

    for (int y2 = 0; y2 < h; y2++) {
      for (int x2 = 0; x2 < w; x2++) {
        String currentName = rows.get(y + y2).cellNames().get(x + x2);
        if (
          !targetName.equals(currentName)
        ) return null;
      }
    }

    return GridArea.create(targetName, x + 1, y + 1, w, h);
  }

}
