package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.List;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;

public class SimpleSelectorMatchers implements DocumentChangeListener {
  
  private final ElementSet allElements = ElementSet.create();
  private final TypeSelectorMatcher typeSelectorMatcher = new TypeSelectorMatcher(allElements);
  private final IdSelectorMatcher idSelectorMatcher = new IdSelectorMatcher();
  private final AttributeOneOfSelectorMatcher attributeOneOfSelectorMatcher = new AttributeOneOfSelectorMatcher(allElements);

  private final List<SimpleSelectorMatcher<?>> allMatchers = List.of(
    typeSelectorMatcher,
    idSelectorMatcher,
    attributeOneOfSelectorMatcher
  );

  public void onNodeAdded(Node node) {
    if (node instanceof Element element) {
      allElements.add(element);
    }

    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onNodeAdded(node);
    }
  }

  public void onNodeRemoved(Node node) {
    if (node instanceof Element element) {
      allElements.remove(element);
    }

    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onNodeRemoved(node);
    }
  }

  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onAttributeChanged(element, attrName, prevValue, newValue);
    }
  }

  public ElementSet match(SelectorPart selectorPart) {
    return switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.match(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.match(typeSelector);
      case AttributeSelector attributeSelector -> switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.match(attributeSelector);
      };
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void addSelectorReference(SelectorPart selectorPart) {
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.addSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.addSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.addSelectorReference(attributeSelector);
      } }
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void removeSelectorReference(SelectorPart selectorPart) {
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.removeSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.removeSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.removeSelectorReference(attributeSelector);
      } }
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

}
