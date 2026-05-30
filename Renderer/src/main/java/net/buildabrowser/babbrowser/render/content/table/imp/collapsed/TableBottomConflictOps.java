package net.buildabrowser.babbrowser.render.content.table.imp.collapsed;

import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.content.table.TableConflictOps;

public class TableBottomConflictOps implements TableConflictOps {

  @Override
  public ComputedBorder get1Border(TableCell cell) {
    return cell.borders().bottomBorder;
  }

  @Override
  public ComputedBorder get2Border(TableCell cell) {
    return cell.borders().topBorder;
  }

  @Override
  public void set1Border(TableCell cell, ComputedBorder border) {
    cell.borders().bottomBorder = border;
  }

  @Override
  public void set2Border(TableCell cell, ComputedBorder border) {
    cell.borders().topBorder = border;
  }

  @Override
  public TableCell getCell(Table table, int locked, int scan, int z) {
    return table.cell(scan, locked, z);
  }

  @Override
  public int lockedStart(TableCell cell) {
    return cell.cellY();
  }

  @Override
  public int lockedRun(TableCell cell) {
    return cell.height();
  }

  @Override
  public int scanStart(TableCell cell) {
    return cell.cellX();
  }

  @Override
  public int scanRun(TableCell cell) {
    return cell.width();
  }

}
