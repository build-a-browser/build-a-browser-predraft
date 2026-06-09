package net.buildabrowser.babbrowser.renderer.fragment.table;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class TableBoxFragment extends UnmanagedBoxFragment<TableBoxFragment> {

  private final Table table;
  private final TableBorderAssignment borderAssignment;

  public TableBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, Table table,
    TableBorderAssignment borderAssignment
  ) {
    super(width, height, inkWidth, inkHeight, box);
    this.table = table;
    this.borderAssignment = borderAssignment;
  }

  public Table table() {
    return this.table;
  }

  public TableBorderAssignment borderAssignment() {
    return this.borderAssignment;
  }
  
}
