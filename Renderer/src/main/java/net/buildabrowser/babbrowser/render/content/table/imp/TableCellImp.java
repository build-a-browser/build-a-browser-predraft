package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableColumn;

// Unfortunately cannot be a record since height and relatedFragment are mutable...
public class TableCellImp implements TableCell {

  private final Table table;
  private final ElementBox cellBox;
  private final int cellX, cellY;
  private final int width;
  
  private int height;
  private UnmanagedBoxFragment relatedFragment;

  public TableCellImp(
    int cellX, int cellY, int width, int height,
    Table table, ElementBox cellBox
  ) {
    this.table = table;
    this.cellBox = cellBox;
    this.cellX = cellX;
    this.cellY = cellY;
    this.width = width;
    this.height = height;
  }

  @Override
  public int cellX() {
    return this.cellX;
  }

  @Override
  public int cellY() {
    return this.cellY;
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public ElementBox cellBox() {
    return this.cellBox;
  }

  public void extend(int heightExtension) {
    this.height += heightExtension;
  }

  @Override
  public void setRelatedFragment(UnmanagedBoxFragment fragment) {
    this.relatedFragment = fragment;
  }

  @Override
  public UnmanagedBoxFragment getRelatedFragment() {
    return this.relatedFragment;
  }

  @Override
  public float minContentContribution(int colNum) {
    // Tbh I don't really get the math here, just doing what the spec says
    float baselineMinContentWidth = baselineMinContentWidth();
    float baselineMaxContentWidth = baselineMaxContentWidth();

    float baselineBorderSpacing = baselineBorderSpacing();
    TableColumn column = table.column(colNum);
    float prevMinContent = column.minContentWidthSpan(width - 1);
    float prevMaxContent = column.maxContentWidthSpan(width - 1);

    float baselineDiff = baselineMaxContentWidth - baselineMinContentWidth;
    float colBaselineRatio = baselineDiff == 0 ? 0 : (prevMaxContent - prevMinContent) / baselineDiff;
    float colBaselineMul = Math.clamp(
      outerMinContentWidth() - baselineMinContentWidth - baselineBorderSpacing,
      0, baselineDiff);
    float colBaselineProduct = colBaselineRatio * colBaselineMul;

    float maxColBaselineRatio = prevMaxContent / baselineMaxContentWidth;
    float minMaxDiff = Math.max(
      // But should this *really* be baselineMaxContentWidth? And not min
      outerMinContentWidth() - baselineMaxContentWidth - baselineBorderSpacing, 0);
    float maxColProduct = maxColBaselineRatio * minMaxDiff;

    return prevMinContent + colBaselineProduct + maxColProduct;
  }

  @Override
  public float maxContentContribution(int colNum) {
    float baselineMaxContentWidth = baselineMaxContentWidth();

    float baselineBorderSpacing = baselineBorderSpacing();
    TableColumn column = table.column(colNum);
    float prevMaxContent = column.maxContentWidthSpan(width - 1);

    float colBaselineRatio = prevMaxContent / baselineMaxContentWidth;
    float maxContentDiff = Math.max(
      outerMaxContentWidth() - baselineMaxContentWidth - baselineBorderSpacing, 0);
    float colBaselineProduct = colBaselineRatio * maxContentDiff;

    return prevMaxContent + colBaselineProduct;
  }

  @Override
  public float outerMinContentWidth() {
    // TODO: Make this actually be "outer"
    return cellBox.dimensions().preferredMinWidthConstraint();
  }

  @Override
  public float outerMaxContentWidth() {
    return cellBox.dimensions().preferredWidthConstraint();
  }

  private float baselineMinContentWidth() {
    // The spec says "Define the baseline min-content width as the sum of the max-content widths [...]"
    // On a similar note, I may be regretting my life choices
    // Addenum: The spec may be bug'd, see https://github.com/w3c/csswg-drafts/issues/9837
    //  As such, I will use min-content instead
    float baselineMinContentWidth = 0;
    for (int x = 0; x < width; x++) {
      baselineMinContentWidth += table
        .column(cellX + x)
        .minContentWidthSpan(width - 1);
    }

    return baselineMinContentWidth;
  }

  private float baselineMaxContentWidth() {
    float baselineMinContentWidth = 0;
    for (int x = 0; x < width; x++) {
      baselineMinContentWidth += table
        .column(cellX + x)
        .maxContentWidthSpan(width - 1);
    }

    return baselineMinContentWidth;
  }

  private float baselineBorderSpacing() {
    // TODO: Compute baseline border spacing
    return 0;
  }

}