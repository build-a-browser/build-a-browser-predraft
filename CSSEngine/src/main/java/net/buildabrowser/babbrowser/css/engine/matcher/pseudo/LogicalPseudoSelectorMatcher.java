package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.imp.CSSSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;

public class LogicalPseudoSelectorMatcher {
  
  private final ElementRootSet allElements;
  private final CSSSelectorMatcher selectorMatcher;

  public LogicalPseudoSelectorMatcher(
    ElementRootSet allElements,
    CSSSelectorMatcher selectorMatcher
  ) {
    this.allElements = allElements;
    this.selectorMatcher = selectorMatcher;
  }

  
  public void addSelectorReference(LogicalPseudoSelector ref) {
    for (ComplexSelector complexSelector: ref.complexSelectors()) {
      selectorMatcher.registerSelector(complexSelector);
    }
  }

  public void removeSelectorReference(LogicalPseudoSelector ref) {
    for (ComplexSelector complexSelector: ref.complexSelectors()) {
      selectorMatcher.unregisterSelector(complexSelector);
    }
  }

  public ElementSet match(LogicalPseudoSelector value) {
    return switch (value.type()) {
      case IS, WHERE -> matchIsElements(value);
      case NOT -> matchNotElements(value);
      case HAS -> matchHasElements(value);
      // TODO: HAS
      default -> throw new UnsupportedOperationException(
        "Unrecognized logical selector type: " + value.type());
    };
  }

  public SelectorSpecificity specificity(LogicalPseudoSelector value) {
    return switch (value.type()) {
      case IS, NOT, HAS -> specificityIs(value);
      case WHERE -> new SelectorSpecificity(0, 0, 0);
      default -> throw new UnsupportedOperationException(
        "Unrecognized logical selector type: " + value.type());
    };
  }

  private ElementSet matchIsElements(LogicalPseudoSelector value) {
    ElementSet matchedElements = allElements.createTemporaryChild();
    for (ComplexSelector complexSelector: value.complexSelectors()) {
      ElementSet submatchElements = selectorMatcher.matchElements(complexSelector);
      matchedElements.union(submatchElements);
    }

    return matchedElements;
  }

  // TODO: Do not match nested :has
  private ElementSet matchNotElements(LogicalPseudoSelector value) {
    ElementSet matchedElements = allElements.createTemporaryChild();
    // TODO: Is this still performant in the SparseBitSet?
    matchedElements.union(allElements);
    for (ComplexSelector complexSelector: value.complexSelectors()) {
      ElementSet submatchElements = selectorMatcher.matchElements(complexSelector);
      matchedElements.difference(submatchElements);
    }

    return matchedElements;
  }

  private ElementSet matchHasElements(LogicalPseudoSelector value) {
    ElementSet matchedElements = allElements.createTemporaryChild();
    for (ComplexSelector complexSelector: value.complexSelectors()) {
      ElementSet submatchElements = selectorMatcher.matchElementsReverse(complexSelector);
      matchedElements.union(submatchElements);
    }

    return matchedElements;
  }

  private SelectorSpecificity specificityIs(LogicalPseudoSelector value) {
    SelectorSpecificity specificity = SelectorSpecificity.ZERO_SPECIFICITY;
    for (ComplexSelector subSelector: value.complexSelectors()) {
      SelectorSpecificity subSpecificity = selectorMatcher.computeSpecificity(subSelector);
      if (SelectorSpecificity.compare(subSpecificity, specificity) > 0) {
        specificity = subSpecificity;
      }
    }

    return specificity;
  }
  
}
