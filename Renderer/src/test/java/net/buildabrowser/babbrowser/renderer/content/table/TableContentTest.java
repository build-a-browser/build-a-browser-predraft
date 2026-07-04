package net.buildabrowser.babbrowser.renderer.content.table;

import static net.buildabrowser.babbrowser.renderer.content.common.test.CommonBoxTestUtil.tableBlockBox;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.doLayout;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.table;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.rowGroup;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.row;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.cell;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.cellRS;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.cellCS;
import static net.buildabrowser.babbrowser.renderer.content.table.test.TableLayoutUtil.cellRSCS;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.test.TableComparator;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;

public class TableContentTest {
  
  @Test
  @DisplayName("Can layout empty table")
  public void canLayoutEmptyTable() {
    ElementBox parentBox = tableBlockBox(List.of());

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    Assertions.assertEquals(0, table.width());
    Assertions.assertEquals(0, table.height());
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with one cell")
  public void canLayoutTableWithOneCell() {
    ElementBox cellBox = cell("A");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f),
      List.of(10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with two columns")
  public void canLayoutTableWithTwoColumns() {
    ElementBox cellBox1 = cell("A");
    ElementBox cellBox2 = cell("B");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1, cellBox2)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f, 5f),
      List.of(10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(1, 0, 0, cellBox2);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with two rows")
  public void canLayoutTableWithTwoRows() {
    ElementBox cellBox1 = cell("A");
    ElementBox cellBox2 = cell("B");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1),
        row(cellBox2)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f),
      List.of(10f, 10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(0, 1, 0, cellBox2);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with two rows and columns")
  public void canLayoutTableWithTwoRowsAndColumns() {
    ElementBox cellBox1 = cell("A");
    ElementBox cellBox2 = cell("B");
    ElementBox cellBox3 = cell("C");
    ElementBox cellBox4 = cell("D");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1, cellBox2),
        row(cellBox3, cellBox4)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f, 5f),
      List.of(10f, 10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(1, 0, 0, cellBox2);
    expectedTable.setElementBox(0, 1, 0, cellBox3);
    expectedTable.setElementBox(1, 1, 0, cellBox4);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with hole")
  public void canLayoutTableWithHole() {
    ElementBox cellBox1 = cell("A");
    ElementBox cellBox2 = cell("B");
    ElementBox cellBox3 = cell("C");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1, cellBox2),
        row(cellBox3)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f, 5f),
      List.of(10f, 10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(1, 0, 0, cellBox2);
    expectedTable.setElementBox(0, 1, 0, cellBox3);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with rowspan")
  public void canLayoutTableWithRowspan() {
    ElementBox cellBox1 = cell("A");
    ElementBox cellBox2 = cellRS(2, "B");
    ElementBox cellBox3 = cell("C");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1, cellBox2),
        row(cellBox3)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f, 5f),
      List.of(10f, 10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(1, 0, 0, cellBox2);
    expectedTable.setElementBox(0, 1, 0, cellBox3);
    expectedTable.setElementBox(1, 1, 0, cellBox2);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with colspan")
  public void canLayoutTableWithColspan() {
    ElementBox cellBox1 = cell("A");
    ElementBox cellBox2 = cell("B");
    ElementBox cellBox3 = cellCS(2, "C");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1, cellBox2),
        row(cellBox3)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    
    TableComparator expectedTable = new TableComparator(
      List.of(5f, 5f),
      List.of(10f, 10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(1, 0, 0, cellBox2);
    expectedTable.setElementBox(0, 1, 0, cellBox3);
    expectedTable.setElementBox(1, 1, 0, cellBox3);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  @Test
  @DisplayName("Can layout table with colspan and rowspan")
  public void canLayoutTableWithColspanAndRowspan() {
    ElementBox cellBox1 = cellRSCS(2, 2, "A");
    ElementBox cellBox2 = cellRS(2, "B");
    ElementBox cellBox3 = cellCS(2, "C");
    ElementBox parentBox = table(
      rowGroup(
        row(cellBox1, cellBox2),
        row(),
        row(cellBox3)));

    TableBoxFragment result = doLayout(parentBox);
    Table table = result.table();
    System.out.println(table.width() + " " + table.height());
    
    TableComparator expectedTable = new TableComparator(
      List.of(2.5f, 2.5f, 5f),
      List.of(5f, 5f, 10f),
      1);
    expectedTable.setElementBox(0, 0, 0, cellBox1);
    expectedTable.setElementBox(0, 1, 0, cellBox1);
    expectedTable.setElementBox(1, 0, 0, cellBox1);
    expectedTable.setElementBox(1, 1, 0, cellBox1);
    
    expectedTable.setElementBox(2, 0, 0, cellBox2);
    expectedTable.setElementBox(2, 1, 0, cellBox2);

    expectedTable.setElementBox(0, 2, 0, cellBox3);
    expectedTable.setElementBox(1, 2, 0, cellBox3);

    expectedTable.compare(table);
    Assertions.assertEquals(List.of(), result.outOfTableFragments());
  }

  // TODO: Test overlap
  // TODO: Need to write a number of sizing tests

}
