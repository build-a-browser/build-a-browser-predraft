package net.buildabrowser.babbrowser.debugger.core.imp;

import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.debugger.core.DebugSideDimensions;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshotBuilder;

public class DebugSnapshotBuilderImp implements DebugSnapshotBuilder {

  private Supplier<PropertyContainer> computedStyles;
  private Supplier<List<WeightedStyleRule>> styleRules;
  private DebugSideDimensions margin;
  private DebugSideDimensions padding;
  private DebugSideDimensions border;

  @Override
  public DebugSnapshotBuilder setComputedStyles(
    Supplier<PropertyContainer> computedStyles
  ) {
    this.computedStyles = computedStyles;
    return this;
  }

  @Override
  public DebugSnapshotBuilder setStyleRules(
    Supplier<List<WeightedStyleRule>> styleRules
  ) {
    this.styleRules = styleRules;
    return this;
  }

  @Override
  public DebugSnapshotBuilder setMargin(DebugSideDimensions margin) {
    this.margin = margin;
    return this;
  }

  @Override
  public DebugSnapshotBuilder setPadding(DebugSideDimensions padding) {
    this.padding = padding;
    return this;
  }

  @Override
  public DebugSnapshotBuilder setBorder(DebugSideDimensions border) {
    this.border = border;
    return this;
  }

  @Override
  public DebugSnapshot build() {
    return new DebugSnapshot(
      computedStyles, styleRules, margin, padding, border);
  }
  
}
