package net.buildabrowser.babbrowser.css.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.css.selector.ComplexSelector;

public record StyleRule(List<ComplexSelector> complexSelectors, List<Declaration> declarations) implements CSSRule {

  public static StyleRule create(List<ComplexSelector> complexSelectors, List<Declaration> declarations) {
    return new StyleRule(complexSelectors, declarations);
  }

}
