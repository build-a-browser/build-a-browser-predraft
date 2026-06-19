package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimpleSelector;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class SimpleSelectorMatchers implements DocumentChangeListener {
  
  private final ElementRootSet allElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  private final TypeSelectorMatcher typeSelectorMatcher;
  private final IdSelectorMatcher idSelectorMatcher;
  private final AttributeSelectorMatcher attributePresentSelectorMatcher;
  private final AttributeOneOfSelectorMatcher attributeOneOfSelectorMatcher;

  private final List<SimpleSelectorMatcher<?>> allMatchers;

  public SimpleSelectorMatchers(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.onSelectorChanged = onSelectorChanged;

    this.typeSelectorMatcher = new TypeSelectorMatcher(allElements, onSelectorChanged);
    this.idSelectorMatcher = new IdSelectorMatcher(allElements, onSelectorChanged);
    this.attributePresentSelectorMatcher = new AttributeSelectorMatcher(allElements, onSelectorChanged);
    this.attributeOneOfSelectorMatcher = new AttributeOneOfSelectorMatcher(allElements, onSelectorChanged);
    
    this.allMatchers = List.of(
      typeSelectorMatcher,
      idSelectorMatcher,
      attributePresentSelectorMatcher,
      attributeOneOfSelectorMatcher
    );
  }

  @Override
  public void onNodeAdded(Node node) {
    if (node instanceof Element element) {
      allElements.add(element);
      onSelectorChanged.accept(UniversalSelector.create());
    }

    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onNodeAdded(node);
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (node instanceof Element element) {
      allElements.remove(element);
      onSelectorChanged.accept(UniversalSelector.create());
    }

    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onNodeRemoved(node);
    }
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    allElements.add(element);
    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onAttributeChanged(element, attrName, prevValue, newValue);
    }
  }

  @Override
  public void onElementEvent(Element element, Event event) {
    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onElementEvent(element, event);
    }
  }

  public ElementSet match(SimpleSelector selectorPart) {
    return switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.match(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.match(typeSelector);
      case AttributeSelector attributeSelector -> switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.match(attributeSelector);
        default -> attributePresentSelectorMatcher.match(attributeSelector);
      };
      case UniversalSelector _1 -> allElements;
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void addSelectorReference(SimpleSelector selectorPart) {
    onSelectorChanged.accept(selectorPart);
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.addSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.addSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.addSelectorReference(attributeSelector);
        default -> attributePresentSelectorMatcher.addSelectorReference(attributeSelector);
      } }
      case UniversalSelector _1 -> {}
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void removeSelectorReference(SimpleSelector selectorPart) {
    onSelectorChanged.accept(selectorPart);
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.removeSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.removeSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.removeSelectorReference(attributeSelector);
        default -> attributePresentSelectorMatcher.removeSelectorReference(attributeSelector);
      } }
      case UniversalSelector _1 -> {}
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

}
