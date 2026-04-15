package net.buildabrowser.babbrowser.css.engine.matcher.psuedo;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.simple.SimpleSelectorMatcher;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePsuedoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public class HoverSelectorMatcher implements SimpleSelectorMatcher<SimplePsuedoSelector> {

  private final ElementSet allElements;
  private final ElementSet matchingElements;

  public HoverSelectorMatcher(ElementRootSet allElements) {
    this.allElements = allElements;
    this.matchingElements = allElements.createChild();
  }

  @Override
  public void addSelectorReference(SimplePsuedoSelector ref) {}

  @Override
  public void removeSelectorReference(SimplePsuedoSelector ref) {}

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    matchingElements.remove(element);
  };

  @Override
  public void onElementEvent(Element element, Event event) {
    if (!event.type().equals("mousemove")) return;

    List<Element> matchedElements = new ArrayList<>();
    Node currentNode = element;
    while (currentNode != null) {
      if (currentNode instanceof Element matchedElement) {
        matchedElements.add(matchedElement);
      }
      currentNode = currentNode.parentNode();
    }

    for (Element oldElement: matchingElements) {
      if (!matchedElements.contains(oldElement)) {
        matchingElements.remove(oldElement);
      }
    }

    for (Element newElement: matchedElements) {
      matchingElements.add(newElement);
    }
  }

  @Override
  public ElementSet match(SimplePsuedoSelector selector) {
    if (!(selector.equals(SimplePsuedoSelector.HOVER))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }
  
}
