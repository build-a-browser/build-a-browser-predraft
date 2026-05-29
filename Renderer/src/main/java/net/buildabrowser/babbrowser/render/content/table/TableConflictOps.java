package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;

public interface TableConflictOps {
  
  ComputedBorder get1Border(TableCell cell);

  ComputedBorder get2Border(TableCell cell);

  void set1Border(TableCell cell, ComputedBorder border);

  void set2Border(TableCell cell, ComputedBorder border);
  
  TableCell getCell(Table table, int locked, int scan, int z);

  int lockedStart(TableCell cell);

  int lockedRun(TableCell cell);

  int scanStart(TableCell cell);

  int scanRun(TableCell cell);

}
