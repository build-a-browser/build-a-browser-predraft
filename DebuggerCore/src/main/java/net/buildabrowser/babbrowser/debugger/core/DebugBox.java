package net.buildabrowser.babbrowser.debugger.core;

import java.util.List;

import org.w3c.dom.Node;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public interface DebugBox {

  Node relatedNode();

  PropertyContainer computedStyles();

  List<WeightedStyleRule> styleRules();
  
  DebugSideDimensions margin();

  DebugSideDimensions padding();

  DebugSideDimensions border();

}
