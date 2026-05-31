package net.buildabrowser.babbrowser.renderer.content.table.imp;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableRow;

public class TableRowImp implements TableRow {

  private final TableComputedBorders borders = new TableComputedBorders();
 
  private final ElementBox rowBox;

  private float usedHeight;

  public TableRowImp(ElementBox rowElement) {
    this.rowBox = rowElement;
  }

  @Override
  public ElementBox rowBox() {
    return this.rowBox;
  }

  @Override
  public TableComputedBorders borders() {
    return this.borders;
  }

  @Override
  public float usedHeight() {
    return this.usedHeight;
  }

  @Override
  public void setUsedHeight(float usedHeight) {
    this.usedHeight = usedHeight;
  }

}
