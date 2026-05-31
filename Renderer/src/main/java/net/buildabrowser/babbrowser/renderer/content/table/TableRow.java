package net.buildabrowser.babbrowser.renderer.content.table;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public interface TableRow {

  ElementBox rowBox();

  TableComputedBorders borders();

  float usedHeight();

  void setUsedHeight(float usedHeight);

}
