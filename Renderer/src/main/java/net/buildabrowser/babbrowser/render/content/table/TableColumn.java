package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public interface TableColumn {
  
  float minContentWidth();

  float minContentWidth(int colSpan);

  float maxContentWidth();

  float maxContentWidth(int colSpan);

  float usedWidth();

  void setUsedWidth(float usedWidth);

  boolean isConstrained();

  // Sizing guesses

  float minContentSizingGuess(LayoutConstraint assignableWidth);

  float minContentPercentageSizingGuess(LayoutConstraint assignableWidth);

  float minContentSpecifiedSizingGuess(LayoutConstraint assignableWidth);

  float maxContentSizingGuess(LayoutConstraint assignableWidth);

  float intrinsicPercentage();

  boolean hasOriginatingCells();

}
