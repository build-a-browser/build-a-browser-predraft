package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.Map;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class PseudoSelectorMatchers implements DocumentChangeListener {
  
  private final ElementRootSet allElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  private final Map<SimplePseudoSelector, SimplePseudoSelectorMatcher> matchers;

  public PseudoSelectorMatchers(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged,
    CSSMatcherContext matcherContext
  ) {
    this.allElements = allElements;
    this.onSelectorChanged = onSelectorChanged;

    this.matchers = Map.of(
      SimplePseudoSelector.ROOT, new RootSelectorMatcher(allElements, onSelectorChanged),
      SimplePseudoSelector.HOVER, new HoverSelectorMatcher(allElements, onSelectorChanged),
      SimplePseudoSelector.LINK, new LinkSelectorMatcher(allElements, onSelectorChanged),
      SimplePseudoSelector.FOCUS, new FocusSelectorMatcher(allElements, onSelectorChanged),
      SimplePseudoSelector.FOCUS_VISIBLE, new FocusVisibleSelectorMatcher(
        allElements, onSelectorChanged, matcherContext),
      SimplePseudoSelector.FOCUS_WITHIN, new FocusWithinSelectorMatcher(allElements, onSelectorChanged)
    );
  }

  @Override
  public void onNodeAdded(Node node) {
    for (SimplePseudoSelectorMatcher matcher: matchers.values()) {
      matcher.onNodeAdded(node);
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    for (SimplePseudoSelectorMatcher matcher: matchers.values()) {
      matcher.onNodeRemoved(node);
    }
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    allElements.add(element);
    for (SimplePseudoSelectorMatcher matcher: matchers.values()) {
      matcher.onAttributeChanged(element, attrName, prevValue, newValue);
    }
  }

  @Override
  public boolean onElementEvent(
    Element element, Event event, boolean allowDefault
  ) {
    for (SimplePseudoSelectorMatcher matcher: matchers.values()) {
      allowDefault = matcher.onElementEvent(element, event, allowDefault);
    }

    return allowDefault;
  }

  public ElementSet match(SimplePseudoSelector selectorPart) {
    return matchers.get(selectorPart).match(selectorPart);
  }

  public void addSelectorReference(SimplePseudoSelector selectorPart) {
    onSelectorChanged.accept(selectorPart);
  }

  public void removeSelectorReference(SimplePseudoSelector selectorPart) {
    onSelectorChanged.accept(selectorPart);
  }

}
