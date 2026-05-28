package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.box.ElementBox;

public interface TableRow {

  float usedHeight();

  void setUsedHeight(float usedHeight);

  ElementBox rowBox();

}
