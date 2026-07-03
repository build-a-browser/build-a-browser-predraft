package net.buildabrowser.babbrowser.css.engine.matcher.pseudo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public class FocusWithinSelectorMatcher implements SimplePseudoSelectorMatcher {

  private final ElementSet allElements;
  private final ElementSet matchingElements;
  private final Consumer<SelectorPart> onSelectorChanged;

  public FocusWithinSelectorMatcher(
    ElementRootSet allElements,
    Consumer<SelectorPart> onSelectorChanged
  ) {
    this.allElements = allElements;
    this.matchingElements = allElements.createChild();
    this.onSelectorChanged = onSelectorChanged;
  }

  @Override
  public void addSelectorReference(SimplePseudoSelector ref) {}

  @Override
  public void removeSelectorReference(SimplePseudoSelector ref) {}

  @Override
  public void onNodeRemoved(Node node) {
    if (!(node instanceof Element element)) return;
    boolean changed = matchingElements.remove(element);
    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS_WITHIN);
    }
  }

  @Override
  public void onElementEvent(Element element, Event event) {
    switch (event.type()) {
      case "focus" -> handleFocusEvent(element);
      case "blur" -> handleBlurEvent();
      default -> {}
    }
  }

  private void handleFocusEvent(Element element) {
    List<Element> matchedElements = new ArrayList<>();
    Node currentNode = element;
    while (currentNode != null) {
      if (currentNode instanceof Element matchedElement) {
        matchedElements.add(matchedElement);
      }
      currentNode = currentNode.parentNode();
    }

    boolean changed = false;
    for (Element oldElement: matchingElements) {
      if (!matchedElements.contains(oldElement)) {
        changed |= matchingElements.remove(oldElement);
      }
    }

    for (Element newElement: matchedElements) {
      changed |= matchingElements.add(newElement);
    }

    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS_WITHIN);
    }
  }

  private void handleBlurEvent() {
    boolean changed = false;
    for (Element oldElement: matchingElements) {
      changed |= matchingElements.remove(oldElement);
    }

    if (changed) {
      onSelectorChanged.accept(SimplePseudoSelector.FOCUS_WITHIN);
    }
  }

  @Override
  public ElementSet match(SimplePseudoSelector selector) {
    if (!(selector.equals(SimplePseudoSelector.FOCUS_WITHIN))) {
      return allElements.root().createTemporaryChild();
    }
    return matchingElements;
  }
  
}
