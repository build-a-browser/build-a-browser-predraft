package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridLineImp;

public interface GridLine {
  
  boolean hasName(String name);

  void addNames(List<String> names);

  List<String> names();

  static GridLine createExplicit() {
    return new GridLineImp(new ArrayList<>());
  }

  static GridLine createImplicit() {
    return new GridLineImp(null);
  }

}
