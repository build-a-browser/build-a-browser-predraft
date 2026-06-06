package net.buildabrowser.babbrowser.css.engine.matcher.simple;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.HoverSelectorMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.LinkSelectorMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.RootSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector.AttributeType;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.UniversalSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class SimpleSelectorMatchers implements DocumentChangeListener {
  
  private final ElementRootSet allElements;
  private final TypeSelectorMatcher typeSelectorMatcher;
  private final IdSelectorMatcher idSelectorMatcher;
  private final AttributeSelectorMatcher attributePresentSelectorMatcher;
  private final AttributeOneOfSelectorMatcher attributeOneOfSelectorMatcher;

  // Psuedo
  private final RootSelectorMatcher rootSelectorMatcher;
  private final HoverSelectorMatcher hoverSelectorMatcher;
  private final LinkSelectorMatcher linkSelectorMatcher;

  private final Map<SimplePseudoSelector, SimpleSelectorMatcher<SimplePseudoSelector>> simplePsuedoSelectors;

  private final List<SimpleSelectorMatcher<?>> allMatchers;
  private final Consumer<SelectorPart> onSelectorChanged;

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

    // TODO: Move these to a PsuedoSelectorMatchers?
    this.rootSelectorMatcher = new RootSelectorMatcher(allElements, onSelectorChanged);
    this.hoverSelectorMatcher = new HoverSelectorMatcher(allElements, onSelectorChanged);
    this.linkSelectorMatcher = new LinkSelectorMatcher(allElements, onSelectorChanged);

    this.simplePsuedoSelectors = Map.of(
      SimplePseudoSelector.ROOT, rootSelectorMatcher,
      SimplePseudoSelector.HOVER, hoverSelectorMatcher,
      SimplePseudoSelector.LINK, linkSelectorMatcher
    );
    
    this.allMatchers = List.of(
      typeSelectorMatcher,
      idSelectorMatcher,
      attributePresentSelectorMatcher,
      attributeOneOfSelectorMatcher,

      // Psuedo
      rootSelectorMatcher,
      hoverSelectorMatcher,
      linkSelectorMatcher
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

  public ElementSet match(SelectorPart selectorPart) {
    return switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.match(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.match(typeSelector);
      case AttributeSelector attributeSelector -> switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.match(attributeSelector);
        default -> attributePresentSelectorMatcher.match(attributeSelector);
      };
      case SimplePseudoSelector pseudoSelector -> simplePsuedoSelectors.get(pseudoSelector).match(pseudoSelector);
      case UniversalSelector _1 -> allElements;
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void addSelectorReference(SelectorPart selectorPart) {
    onSelectorChanged.accept(selectorPart);
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.addSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.addSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.addSelectorReference(attributeSelector);
        default -> attributePresentSelectorMatcher.addSelectorReference(attributeSelector);
      } }
      case Combinator _1 -> {}
      case UniversalSelector _1 -> {}
      case SimplePseudoSelector _1 -> {}
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

  public void removeSelectorReference(SelectorPart selectorPart) {
    onSelectorChanged.accept(selectorPart);
    switch (selectorPart) {
      case IdSelector idSelector -> idSelectorMatcher.removeSelectorReference(idSelector);
      case TypeSelector typeSelector -> typeSelectorMatcher.removeSelectorReference(typeSelector);
      case AttributeSelector attributeSelector -> { switch (attributeSelector.type()) {
        case AttributeType.ONE_OF -> attributeOneOfSelectorMatcher.removeSelectorReference(attributeSelector);
        default -> attributePresentSelectorMatcher.removeSelectorReference(attributeSelector);
      } }
      case Combinator _1 -> {}
      case UniversalSelector _1 -> {}
      case SimplePseudoSelector _1 -> {}
      default -> throw new UnsupportedOperationException("Don't recognize that selector type! " + selectorPart);
    };
  }

}
