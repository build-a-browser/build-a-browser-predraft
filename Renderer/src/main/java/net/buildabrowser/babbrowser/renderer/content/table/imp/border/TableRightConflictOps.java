package net.buildabrowser.babbrowser.renderer.content.table.imp.border;

import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableConflictOps;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.ComputedBorder;

public class TableRightConflictOps implements TableConflictOps {

  @Override
  public ComputedBorder get1Border(TableCell cell) {
    return cell.borders().rightBorder;
  }

  @Override
  public ComputedBorder get2Border(TableCell cell) {
    return cell.borders().leftBorder;
  }

  @Override
  public void set1Border(TableCell cell, ComputedBorder border) {
    cell.borders().rightBorder = border;
  }

  @Override
  public void set2Border(TableCell cell, ComputedBorder border) {
    cell.borders().leftBorder = border;
  }

  @Override
  public TableCell getCell(Table table, int locked, int scan, int z) {
    return table.cell(locked, scan, z);
  }

  @Override
  public int lockedStart(TableCell cell) {
    return cell.cellX();
  }

  @Override
  public int lockedRun(TableCell cell) {
    return cell.width();
  }

  @Override
  public int scanStart(TableCell cell) {
    return cell.cellY();
  }

  @Override
  public int scanRun(TableCell cell) {
    return cell.height();
  }

  @Override
  public int lockedRun(Table table) {
    return table.width();
  }

}
