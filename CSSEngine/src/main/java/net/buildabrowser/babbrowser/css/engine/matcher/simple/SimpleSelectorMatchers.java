package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.psuedo.HoverSelectorMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.psuedo.RootSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePsuedoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class SimpleSelectorMatchers implements DocumentChangeListener {
  
  private final ElementRootSet allElements = ElementSet.createRoot();
  private final TypeSelectorMatcher typeSelectorMatcher = new TypeSelectorMatcher(allElements);
  private final IdSelectorMatcher idSelectorMatcher = new IdSelectorMatcher(allElements);
  private final AttributeSelectorMatcher attributePresentSelectorMatcher = new AttributeSelectorMatcher(allElements);
  private final AttributeOneOfSelectorMatcher attributeOneOfSelectorMatcher = new AttributeOneOfSelectorMatcher(allElements);

  // Psuedo
  private final RootSelectorMatcher rootSelectorMatcher = new RootSelectorMatcher(allElements);
  private final HoverSelectorMatcher hoverSelectorMatcher = new HoverSelectorMatcher(allElements);

  private final Map<SimplePsuedoSelector, SimpleSelectorMatcher<SimplePsuedoSelector>> simplePsuedoSelectors = Map.of(
    SimplePsuedoSelector.ROOT, rootSelectorMatcher,
    SimplePsuedoSelector.HOVER, hoverSelectorMatcher
  );

  private final List<SimpleSelectorMatcher<?>> allMatchers = List.of(
    typeSelectorMatcher,
    idSelectorMatcher,
    attributePresentSelectorMatcher,
    attributeOneOfSelectorMatcher,

    // Psuedo
    rootSelectorMatcher,
    hoverSelectorMatcher
  );

  public SimpleSelectorMatchers(Runnable onChange) {
    allElements.attachChangeListener(onChange);
  }

  @Override
  public void onNodeAdded(Node node) {
    if (node instanceof Element element) {
      allElements.add(element);
    }

    for (SimpleSelectorMatcher<?> matcher: allMatchers) {
      matcher.onNodeAdded(node);
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (node instanceof Element element) {
      allElements.remove(element);
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

  public ElementSet match(SelectorPart selectorPart) {
    return switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.match(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.match(typeSelector);
      case AttributeSelector attributeSelector -> switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.match(attributeSelector);
        default -> attributePresentSelectorMatcher.match(attributeSelector);
      };
      case SimplePsuedoSelector psuedoSelector -> simplePsuedoSelectors.get(psuedoSelector).match(psuedoSelector);
      case UniversalSelector _ -> allElements;
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void addSelectorReference(SelectorPart selectorPart) {
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.addSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.addSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.addSelectorReference(attributeSelector);
        default -> attributePresentSelectorMatcher.addSelectorReference(attributeSelector);
      } }
      case Combinator _, UniversalSelector _, SimplePsuedoSelector _ -> {}
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void removeSelectorReference(SelectorPart selectorPart) {
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.removeSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.removeSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.removeSelectorReference(attributeSelector);
        default -> attributePresentSelectorMatcher.removeSelectorReference(attributeSelector);
      } }
      case Combinator _, UniversalSelector _, SimplePsuedoSelector _ -> {}
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

}
