package net.buildabrowser.babbrowser.debugger.core;

import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public interface DebugSnapshotBuilder {
  
  DebugSnapshotBuilder setComputedStyles(Supplier<PropertyContainer> computedStyles);

  DebugSnapshotBuilder setStyleRules(Supplier<List<WeightedStyleRule>> styleRules);

  DebugSnapshotBuilder setMargin(DebugSideDimensions margin);

  DebugSnapshotBuilder setPadding(DebugSideDimensions padding);

  DebugSnapshotBuilder setBorder(DebugSideDimensions border);

  DebugSnapshot build();
  
}
