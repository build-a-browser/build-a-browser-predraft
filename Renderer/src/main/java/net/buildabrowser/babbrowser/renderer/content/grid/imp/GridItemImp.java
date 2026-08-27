package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.GridDirection;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public class GridItemImp implements GridItem {
  
  private final ElementBox itemBox;

  private Integer colStart;
  private Integer colEnd;
  private Integer rowStart;
  private Integer rowEnd;
  private int fallbackSpan;
  private UnmanagedBoxFragment<?> relatedFragment;

  public GridItemImp(ElementBox itemBox) {
    this.itemBox = itemBox;
  }

  @Override
  public Integer colLineStart() {
    return this.colStart;
  }

  @Override
  public Integer colLineEnd() {
    return this.colEnd;
  }

  @Override
  public Integer rowLineStart() {
    return this.rowStart;
  }

  @Override
  public Integer rowLineEnd() {
    return this.rowEnd;
  }

  @Override
  public Integer lineStart(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> colLineStart();
      case ROW -> rowLineStart();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  @Override
  public Integer lineEnd(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> colLineEnd();
      case ROW -> rowLineEnd();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  @Override
  public void setSpan(
    Integer colStart, Integer colEnd,
    Integer rowStart, Integer rowEnd
  ) {
    this.colStart = colStart;
    this.colEnd = colEnd;
    this.rowStart = rowStart;
    this.rowEnd = rowEnd;
  }

  @Override
  public int fallbackSpan() {
    return this.fallbackSpan;
  }

  @Override
  public void setFallbackSpan(int fallbackSpan) {
    this.fallbackSpan = fallbackSpan;
  }

  @Override
  public ElementBox box() {
    return this.itemBox;
  }

  @Override
  public void setRelatedFragment(UnmanagedBoxFragment<?> fragment) {
    this.relatedFragment = fragment;
  }

  @Override
  public UnmanagedBoxFragment<?> fragment() {
    return this.relatedFragment;
  }

  @Override
  public float crossSize() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'crossSize'");
  }

  @Override
  public void setCrossPos(float itemCrossPos) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setCrossPos'");
  }

}
