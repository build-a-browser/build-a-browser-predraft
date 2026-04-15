package net.buildabrowser.babbrowser.css.engine.matcher.psuedo;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.simple.SimpleSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePsuedoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class RootSelectorMatcher implements SimpleSelectorMatcher<SimplePsuedoSelector> {

  private final ElementSet allElements;
  private final ElementSet matchingElements;

  public RootSelectorMatcher(ElementRootSet allElements) {
    this.allElements = allElements;
    this.matchingElements = allElements.createChild();
  }

  @Override
  public void addSelectorReference(SimplePsuedoSelector ref) {}

  @Override
  public void removeSelectorReference(SimplePsuedoSelector ref) {}

  @Override
  public void onNodeAdded(Node node) {
    if (!(node instanceof Element element)) return;
    if (matches(element)) {
      matchingElements.add(element);
    }
  };

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    matchingElements.remove(element);
  };

  @Override
  public ElementSet match(SimplePsuedoSelector selector) {
    if (!(selector.equals(SimplePsuedoSelector.ROOT))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }

  private boolean matches(Element element) {
    // TODO: Instead do instanceof HTMLElement, once that's a thing
    return element.name().equals("html");
  }
  
}
