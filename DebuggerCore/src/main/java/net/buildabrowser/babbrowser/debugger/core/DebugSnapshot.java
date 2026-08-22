package net.buildabrowser.babbrowser.debugger.core;

import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.debugger.core.imp.DebugSnapshotBuilderImp;

public record DebugSnapshot(
  Supplier<PropertyContainer> computedStyles,
  Supplier<List<WeightedStyleRule>> styleRules,
  DebugSideDimensions margin,
  DebugSideDimensions padding,
  DebugSideDimensions border
) {
  
  public static DebugSnapshotBuilder builder() {
    return new DebugSnapshotBuilderImp();
  }

}
