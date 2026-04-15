package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;

public record StyleRule(List<ComplexSelector> complexSelectors, List<Declaration> declarations) implements CSSRule {


}
