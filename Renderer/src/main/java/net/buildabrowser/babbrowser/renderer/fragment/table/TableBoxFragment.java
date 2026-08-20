package net.buildabrowser.babbrowser.renderer.fragment.table;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public abstract class TableBoxFragment extends UnmanagedBoxFragment<TableBoxFragment> {

  private final Table table;
  private final TableBorderAssignment borderAssignment;
  private final List<PosRefBoxFragment> outOfTableFragments;

  public TableBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, Table table,
    TableBorderAssignment borderAssignment,
    List<PosRefBoxFragment> outOfTableFragments
  ) {
    // TODO: Require list of nested fragments
    super(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, null);
    this.table = table;
    this.borderAssignment = borderAssignment;
    this.outOfTableFragments = outOfTableFragments;
  }

  public Table table() {
    return this.table;
  }

  public TableBorderAssignment borderAssignment() {
    return this.borderAssignment;
  }

  public List<PosRefBoxFragment> outOfTableFragments() {
    return this.outOfTableFragments;
  }
  
}
