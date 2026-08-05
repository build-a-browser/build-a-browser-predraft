package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public class GridItemImp implements GridItem {
  
  private final ElementBox itemBox;

  private Integer colStart;
  private Integer colEnd;
  private Integer rowStart;
  private Integer rowEnd;

  public GridItemImp(ElementBox itemBox) {
    this.itemBox = itemBox;
  }

  @Override
  public Integer colStart() {
    return this.colStart;
  }

  @Override
  public Integer colEnd() {
    return this.colEnd;
  }

  @Override
  public Integer rowStart() {
    return this.rowStart;
  }

  @Override
  public Integer rowEnd() {
    return this.rowEnd;
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
  public ElementBox itemBox() {
    return this.itemBox;
  }

  @Override
  public void setRelatedFragment(UnmanagedBoxFragment<?> fragment) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setRelatedFragment'");
  }

}
