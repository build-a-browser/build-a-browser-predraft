package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import net.buildabrowser.babbrowser.renderer.content.grid.BackingGrid;
import net.buildabrowser.babbrowser.renderer.content.grid.GridSpan;

public class BackingGridImp<T> implements BackingGrid<T> {

  private final BackingArraySupplier<T> arraySupplier;

  private GridSpan span;
  private T[][][] backingArray;

  public BackingGridImp(
    BackingArraySupplier<T> arraySupplier
  ) {
    this.arraySupplier = arraySupplier;
  }

  @Override
  public T item(int x, int y, int z) {
    return backingArray[z][adjustY(y)][adjustX(x)];
  }

  @Override
  public void set(int x, int y, int z, T item) {
    backingArray[z][adjustY(y)][adjustX(x)] = item;
  }

  // TODO: Separate grid size and capacity, use power-of-two capacity resizes to reduce copies
  @Override
  public void resize(GridSpan span) {
    if (this.backingArray == null || this.span == null) {
      this.backingArray = arraySupplier.get(span.width(), span.height(), 1);
      this.span = span;
      return;
    }

    assert span.rowStart() <= this.span.rowStart();
    assert span.rowEnd() >= this.span.rowEnd();
    assert span.colStart() <= this.span.colStart();
    assert span.colEnd() >= this.span.colEnd();

    int layers = backingArray == null ? 1 : backingArray.length;
    T[][][] newBackingArray = arraySupplier.get(span.width(), span.height(), layers);

    int rowDiff = this.span.rowStart() - span.rowStart();
    int colDiff = this.span.colStart() - span.colStart();

    for (int z = 0; z < backingArray.length; z++) {
      T[][] layerSource = backingArray[z];
      T[][] layerTarget = newBackingArray[z];
      for (int y = 0; y < layerSource.length; y++) {
        System.arraycopy(layerSource[y], 0, layerTarget[y + rowDiff], colDiff, this.span.width());
      }
    }

    this.backingArray = newBackingArray;
    this.span = span;
  }

  @Override
  public void resizeLayers(int layers) {
    assert layers >= backingArray.length;

    T[][][] newBackingArray = arraySupplier.get(span.width(), span.height(), layers);

    for (int z = 0; z < backingArray.length; z++) {
      newBackingArray[z] = backingArray[z];
    }

    this.backingArray = newBackingArray;
  }

  @Override
  public GridSpan span() {
    return this.span;
  }

  @Override
  public int layers() {
    return backingArray.length;
  }

  private int adjustX(int x) {
    return x - span.colStart();
  }

  private int adjustY(int y) {
    return y - span.rowStart();
  }

  public static interface BackingArraySupplier<T> {

    T[][][] get(int w, int h, int d);

  }
  
}
