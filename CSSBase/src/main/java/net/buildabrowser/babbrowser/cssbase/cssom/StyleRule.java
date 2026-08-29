package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;

public record StyleRule(
  List<ComplexSelector> sourceSelectors,
  List<ComplexSelector> complexSelectors,
  List<Declaration> declarations,
  List<CSSRule> nestedRules
) implements CSSRule {

  public StyleRule(
    List<Declaration> declarations
  ) {
    this(List.of(), List.of(), declarations, List.of());
  }

}
