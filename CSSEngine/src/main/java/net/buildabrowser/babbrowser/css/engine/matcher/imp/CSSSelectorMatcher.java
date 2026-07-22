package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.LogicalPseudoSelectorMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.NthChildPseudoSelectorMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.PseudoSelectorMatchers;
import net.buildabrowser.babbrowser.css.engine.matcher.simple.SimpleSelectorMatchers;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NextSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.NthChildPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SimpleSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SubsequentSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.ForkedDocumentChangeListener;

public class CSSSelectorMatcher {
  
  private final ElementRootSet allElements;
  private final SimpleSelectorMatchers simpleMatchers;
  private final PseudoSelectorMatchers pseudoMatchers;
  private final LogicalPseudoSelectorMatcher logicalPseudoMatchers;
  private final NthChildPseudoSelectorMatcher childPseudoMatchers;
  private final CombinatorMatchers combinatorMatchers;

  public CSSSelectorMatcher(
    ElementRootSet allElements,
    CSSMatcherContext context,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.simpleMatchers = new SimpleSelectorMatchers(allElements,
      onSelectorChanged);
    this.pseudoMatchers = new PseudoSelectorMatchers(
      allElements, onSelectorChanged, context);
    this.logicalPseudoMatchers = new LogicalPseudoSelectorMatcher(allElements, this);
    this.childPseudoMatchers = new NthChildPseudoSelectorMatcher(
      allElements, this, onSelectorChanged);
    this.combinatorMatchers = new CombinatorMatchers(allElements);;
  }

  public ElementSet matchElements(ComplexSelector complexSelector) {
    List<SelectorPart> parts = complexSelector.parts();
    if (parts.size() == 0) return null;
    ElementSet currentMatched = match(parts.get(0)).copy();
    for (int i = 1; i < parts.size(); i++) {
      SelectorPart part = parts.get(i);
      if (part instanceof Combinator combinator) {
        ElementSet nextMatched = match(parts.get(++i));
        currentMatched = matchCombinator(combinator, currentMatched, nextMatched);
      } else if (part instanceof SimplePseudoElement) {
        if (i != parts.size() - 1) return null;
      } else {
        currentMatched.intersect(match(part));
      }
    }

    return currentMatched;
  }

  public ElementSet matchElementsReverse(ComplexSelector complexSelector) {
    List<SelectorPart> parts = complexSelector.parts();
    if (parts.size() == 0) return null;
    ElementSet currentMatched = match(parts.get(parts.size() - 1)).copy();
    for (int i = parts.size() - 2; i >= 0; i--) {
      SelectorPart part = parts.get(i);
      if (part instanceof Combinator combinator) {
        currentMatched = matchCombinatorReversed(combinator, currentMatched);
      } else if (part instanceof SimplePseudoElement) {
        if (i != parts.size() - 1) return null;
      } else {
        currentMatched.intersect(match(part));
      }
    }

    return currentMatched;
  }

  // TODO: Revamp the code to compute specificity
  public SelectorSpecificity computeSpecificity(ComplexSelector selector) {
    int numIdSelectors = 0;
    int numClassSelectors = 0;
    int numTypeSelectors = 0;
    for (SelectorPart selectorPart: selector.parts()) {
      switch (selectorPart) {
        case IdSelector _1 -> numIdSelectors++;
        case AttributeSelector _1 -> numClassSelectors++;
        case TypeSelector _1 -> numTypeSelectors++;
        case LogicalPseudoSelector logicalPseudoSelector -> {
          SelectorSpecificity subSpecificity = logicalPseudoMatchers.specificity(logicalPseudoSelector);
          numIdSelectors += subSpecificity.numIdSelectors();
          numClassSelectors += subSpecificity.numClassSelectors();
          numTypeSelectors += subSpecificity.numTypeSelectors();
        }
        // TODO: nth-child?
        default -> {}
      }
    }

    return new SelectorSpecificity(numIdSelectors, numClassSelectors, numTypeSelectors);
  }

  public void registerSelector(ComplexSelector selector) {
    for (SelectorPart selectorPart: selector.parts()) {
      switch (selectorPart) {
        case SimpleSelector simpleSelector -> simpleMatchers.addSelectorReference(simpleSelector);
        case SimplePseudoSelector simplePseudoSelector -> pseudoMatchers.addSelectorReference(simplePseudoSelector);
        case LogicalPseudoSelector logicalPseudoSelector -> logicalPseudoMatchers.addSelectorReference(logicalPseudoSelector);
        case NthChildPseudoSelector nthChildPseudoSelector -> childPseudoMatchers.addSelectorReference(nthChildPseudoSelector);
        case SimplePseudoElement _1 -> {}
        case Combinator _1 -> {}
        default -> throw new UnsupportedOperationException(
          "Unrecognized selector type: " + selectorPart);
      }
    }
  }

  public void unregisterSelector(ComplexSelector selector) {
    for (SelectorPart selectorPart: selector.parts()) {
      switch (selectorPart) {
        case SimpleSelector simpleSelector -> simpleMatchers.removeSelectorReference(simpleSelector);
        case SimplePseudoSelector simplePseudoSelector -> pseudoMatchers.removeSelectorReference(simplePseudoSelector);
        case LogicalPseudoSelector logicalPseudoSelector -> logicalPseudoMatchers.removeSelectorReference(logicalPseudoSelector);
        case NthChildPseudoSelector nthChildPseudoSelector -> childPseudoMatchers.removeSelectorReference(nthChildPseudoSelector);
        case SimplePseudoElement _1 -> {}
        case Combinator _1 -> {}
        default -> throw new UnsupportedOperationException(
          "Unrecognized selector type: " + selectorPart);
      }
    }
  }

  public DocumentChangeListener documentChangeListener() {
    return new ForkedDocumentChangeListener(
      simpleMatchers,
      pseudoMatchers,
      childPseudoMatchers
    );
  }

  private ElementSet match(SelectorPart selectorPart) {
    return switch (selectorPart) {
      case SimpleSelector simpleSelector -> simpleMatchers.match(simpleSelector);
      case SimplePseudoSelector simplePseudoSelector -> pseudoMatchers.match(simplePseudoSelector);
      case LogicalPseudoSelector logicalPseudoSelector -> logicalPseudoMatchers.match(logicalPseudoSelector);
      case NthChildPseudoSelector nthChildPseudoSelector -> childPseudoMatchers.match(nthChildPseudoSelector);
      case SimplePseudoElement _1 -> allElements;
      case Combinator _1 -> allElements.createTemporaryChild();
      default -> throw new UnsupportedOperationException(
        "Unrecognized selector type: " + selectorPart);
    };
  }

  private ElementSet matchCombinator(Combinator combinator, ElementSet currentMatched, ElementSet nextMatched) {
    return switch (combinator) {
      case DescendantCombinator _1 -> combinatorMatchers.matchDescendants(currentMatched, nextMatched);
      case ChildCombinator _1 -> combinatorMatchers.matchChild(currentMatched, nextMatched);
      case NextSiblingCombinator _1 -> combinatorMatchers.matchNextSibling(currentMatched, nextMatched);
      case SubsequentSiblingCombinator _1 -> combinatorMatchers.matchSubsequentSibling(currentMatched, nextMatched);
      default -> throw new IllegalArgumentException();
    };
  }

  private ElementSet matchCombinatorReversed(Combinator combinator, ElementSet currentMatched) {
    CombinatorMatchersReversed matchers = combinatorMatchers.reversed();
    return switch (combinator) {
      case DescendantCombinator _1 -> matchers.matchDescendants(currentMatched);
      case ChildCombinator _1 -> matchers.matchChild(currentMatched);
      case NextSiblingCombinator _1 -> matchers.matchNextSibling(currentMatched);
      case SubsequentSiblingCombinator _1 -> matchers.matchSubsequentSibling(currentMatched);
      default -> throw new IllegalArgumentException();
    };
  }

  // For testing
  public LogicalPseudoSelectorMatcher logicalPseudoMatchers() {
    return this.logicalPseudoMatchers;
  }

  public NthChildPseudoSelectorMatcher childPseudoMatchers() {
    return this.childPseudoMatchers;
  }

}
