package net.buildabrowser.babbrowser.renderer.content.grid;

public interface BackingGrid<T> {
  
  T item(int x, int y, int z);

  void set(int x, int y, int z, T item);

  void resize(GridSpan span);

  void resizeLayers(int layers);

  GridSpan span();

  int layers();

}
