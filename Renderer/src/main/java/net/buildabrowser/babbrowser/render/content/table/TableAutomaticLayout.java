package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;

public final class TableAutomaticLayout {
  
  private TableAutomaticLayout() {}

  public static UnmanagedBoxFragment layout(
    Table table,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    float[] columnMinWidths = widthConstraint.type().equals(LayoutConstraintType.MAX_CONTENT) ? null :
      computeColumnMinContentWidths(table, widthConstraint);
    float[] columnPreferredWidths = widthConstraint.type().equals(LayoutConstraintType.MIN_CONTENT) ? columnMinWidths :
      computeColumnMinContentWidths(table, widthConstraint);
    columnMinWidths = columnMinWidths == null ? columnPreferredWidths : columnMinWidths;
    
    return null;
  }

  private static float[] computeColumnMinContentWidths(Table table, LayoutConstraint widthConstraint) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'computeColumnMinwidth(Measurement.CONTENT)s'");
  }

}
