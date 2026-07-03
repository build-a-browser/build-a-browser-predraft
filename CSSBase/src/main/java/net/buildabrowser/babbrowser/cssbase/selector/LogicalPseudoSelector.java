package net.buildabrowser.babbrowser.cssbase.selector;

import java.util.List;

public record LogicalPseudoSelector(
  LogicalPseudoSelectorType type,
  List<ComplexSelector> complexSelectors
) implements SelectorPart {

  public enum LogicalPseudoSelectorType {
    IS, NOT, WHERE, HAS
  }

}
