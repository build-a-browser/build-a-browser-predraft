package net.buildabrowser.babbrowser.render.content.table;

public interface TableColumn {
  
  float minContentWidth();

  float minContentWidthSpan(int colSpan);

  float maxContentWidth();

  float maxContentWidthSpan(int colSpan);

  float usedWidth();

}
