package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.box.ElementBox;

public interface TableRow {

  ElementBox rowBox();

  TableComputedBorders borders();

  float usedHeight();

  void setUsedHeight(float usedHeight);

}
