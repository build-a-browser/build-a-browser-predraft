package net.buildabrowser.babbrowser.renderer.content.table.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableColumn;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

// Unfortunately cannot be a record since height and relatedFragment are mutable...
public class TableCellImp implements TableCell {

  private final Table table;
  private final ElementBox cellBox;
  private final int cellX, cellY;
  private final int width;

  private final TableComputedBorders borders = new TableComputedBorders();
  
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
  public TableComputedBorders borders() {
    return this.borders;
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
    float prevMinContent = column.minContentWidth(width - 1);
    float prevMaxContent = column.maxContentWidth(width - 1);

    float baselineDiff = baselineMaxContentWidth - baselineMinContentWidth;
    float colBaselineRatio = baselineDiff == 0 ? 0 : (prevMaxContent - prevMinContent) / baselineDiff;
    float colBaselineMul = mathClamp(
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
    float prevMaxContent = column.maxContentWidth(width - 1);

    float colBaselineRatio = prevMaxContent / baselineMaxContentWidth;
    float maxContentDiff = Math.max(
      outerMaxContentWidth() - baselineMaxContentWidth - baselineBorderSpacing, 0);
    float colBaselineProduct = colBaselineRatio * maxContentDiff;

    return prevMaxContent + colBaselineProduct;
  }

  // TODO: Is evaluateAdjustedSize and AUTO proper to use here?
  @Override
  public float outerMinContentWidth() {
    // TODO: Make this actually be "outer"
    PropertyContainer properties = cellBox.properties();
    float minContentWidth = cellBox.dimensions().preferredMinWidthConstraint();
    LayoutConstraint specifiedMinWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      LayoutConstraint.AUTO, cellBox, properties.get(CSSProperty.MIN_WIDTH));

    if (specifiedMinWidth.isBounded()) {
      return outerWidth(Math.max(specifiedMinWidth.value(), minContentWidth));
    }
    return outerWidth(minContentWidth);
  }

  @Override
  public float outerMaxContentWidth() {
    PropertyContainer properties = cellBox.properties();
    float minContentWidth = cellBox.dimensions().preferredMinWidthConstraint();
    float maxContentWidth = cellBox.dimensions().preferredWidthConstraint();
    LayoutConstraint specifiedWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      LayoutConstraint.AUTO, cellBox);
    LayoutConstraint specifiedMinWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      LayoutConstraint.AUTO, cellBox, properties.get(CSSProperty.MIN_WIDTH));
    LayoutConstraint specifiedMaxWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      LayoutConstraint.AUTO, cellBox, properties.get(CSSProperty.MAX_WIDTH));

    float usedWidth = minContentWidth;
    if (specifiedMinWidth.isBounded()) {
      usedWidth = Math.max(usedWidth, specifiedMinWidth.value());
    }
    if (specifiedWidth.isBounded()) {
      usedWidth = Math.max(usedWidth, specifiedWidth.value());
    }

    boolean isConstrained = width == 1 && table.column(cellX).isConstrained();
    if (specifiedMaxWidth.isBounded() && specifiedWidth.isBounded()) {
      usedWidth = Math.max(usedWidth, Math.min(specifiedMaxWidth.value(), specifiedWidth.value()));
    } else if (specifiedMaxWidth.isBounded() && isConstrained) {
      usedWidth = Math.max(usedWidth, specifiedMaxWidth.value());
    } else if (specifiedMaxWidth.isBounded()) {
      usedWidth = Math.max(usedWidth, Math.min(specifiedMaxWidth.value(), maxContentWidth));
    } else if (!isConstrained) {
      usedWidth = Math.max(usedWidth, maxContentWidth);
    }

    return outerWidth(usedWidth);
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
        .minContentWidth(width - 1);
    }

    return baselineMinContentWidth;
  }

  private float baselineMaxContentWidth() {
    float baselineMinContentWidth = 0;
    for (int x = 0; x < width; x++) {
      baselineMinContentWidth += table
        .column(cellX + x)
        .maxContentWidth(width - 1);
    }

    return baselineMinContentWidth;
  }

  private float outerWidth(float innerWidth) {
    float[] padding = cellBox.dimensions().getComputedPadding();
    float totalHPadding = padding[2] + padding[3];
    float[] border = cellBox.dimensions().getComputedBorder();
    float totalHBorder = border[2] + border[3];

    return innerWidth + totalHPadding + totalHBorder;
  }

  private float baselineBorderSpacing() {
    return table.spacings().hSpace() * (width - 1);
  }

}