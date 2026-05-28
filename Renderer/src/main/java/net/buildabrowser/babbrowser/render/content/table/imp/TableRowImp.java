package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.table.TableRow;

public class TableRowImp implements TableRow {
 
  private final ElementBox rowBox;

  private float usedHeight;

  public TableRowImp(ElementBox rowElement) {
    this.rowBox = rowElement;
  }

  @Override
  public float usedHeight() {
    return this.usedHeight;
  }

  @Override
  public void setUsedHeight(float usedHeight) {
    this.usedHeight = usedHeight;
  }

  @Override
  public ElementBox rowBox() {
    return this.rowBox;
  }

}
