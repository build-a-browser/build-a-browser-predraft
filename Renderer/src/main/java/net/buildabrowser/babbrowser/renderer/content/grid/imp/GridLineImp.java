package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.content.grid.GridLine;

public class GridLineImp implements GridLine {

  private final List<String> names;

  public GridLineImp(
    // If non-null, names should be mutable
    List<String> names
  ) {
    this.names = names;
  }

  @Override
  public boolean hasName(String name) {
    return names == null || names.contains(name);
  }

  @Override
  public void addNames(List<String> names) {
    this.names.addAll(names);
  }

  @Override
  public List<String> names() {
    return this.names;
  }
  
}
